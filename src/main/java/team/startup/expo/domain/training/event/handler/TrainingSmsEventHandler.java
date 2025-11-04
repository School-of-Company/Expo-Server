package team.startup.expo.domain.training.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.training.entity.Category;
import team.startup.expo.domain.training.entity.TrainingProgram;
import team.startup.expo.domain.training.event.TrainingSmsEvent;
import team.startup.expo.domain.training.exception.InvalidTrainingSectionException;
import team.startup.expo.domain.training.exception.RequiredKeynoteOrTeacherMissingException;
import team.startup.expo.global.sms.SmsProperties;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainingSmsEventHandler {

    private final ExpoRepository expoRepository;
    private final DefaultMessageService messageService;
    private final SmsProperties smsProperties;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Async("asyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendTrainingSmsHandler(TrainingSmsEvent event) {
        try {
            expoRepository.findById(event.getExpoId())
                    .orElseThrow(NotFoundExpoException::new);

            List<TrainingProgram> programs = event.getTrainingPrograms();

            List<TrainingProgram> essential = programs.stream()
                    .filter(p -> p.getCategory() == Category.ESSENTIAL)
                    .collect(Collectors.toList());

            List<TrainingProgram> electiveAll = programs.stream()
                    .filter(p -> p.getCategory() == Category.CHOICE)
                    .collect(Collectors.toList());

            if (essential.isEmpty()) throw new RequiredKeynoteOrTeacherMissingException();

            List<TrainingProgram> elective21 = electiveAll.stream()
                    .filter(p -> isOnDay(p, 21))
                    .sorted(byStartDateTimeThenTitle())
                    .collect(Collectors.toList());

            List<TrainingProgram> elective22 = electiveAll.stream()
                    .filter(p -> isOnDay(p, 22))
                    .sorted(byStartDateTimeThenTitle())
                    .collect(Collectors.toList());

            if (elective21.size() > 2) throw new InvalidTrainingSectionException();
            if (elective22.size() > 4) throw new InvalidTrainingSectionException();

            List<TrainingProgram> elective = new ArrayList<>();
            elective.addAll(elective21.stream().limit(2).toList());
            elective.addAll(elective22.stream().limit(4).toList());

            boolean hasEssential21 = essential.stream().anyMatch(p -> isOnDay(p, 21));
            boolean hasEssential22 = essential.stream().anyMatch(p -> isOnDay(p, 22));

            int c21 = elective21.size();
            int c22 = elective22.size();

            List<Integer> giNumbers = new ArrayList<>();

            if (hasEssential21 && c21 > 0) {
                int idx = clamp(c21, 1, 2);
                giNumbers.add(idx);           // 1~2기
            }
            if (hasEssential22 && c22 > 0) {
                int idx = clamp(c22, 1, 4);
                giNumbers.add(2 + idx);       // 3~6기
            }

            int[] giHours = {2, 3, 2, 3, 4, 5, 2, 3, 4, 5};

            String giPart = giNumbers.isEmpty()
                    ? "미정"
                    : giNumbers.stream()
                    .map(n -> {
                        int hours = (n >= 1 && n <= 10) ? giHours[n - 1] : 0;
                        return String.format("%d기(%d시간)", n, hours);
                    })
                    .collect(Collectors.joining(", "));

            String commonLine = essential.stream()
                    .sorted(byStartDateTimeThenTitle())
                    .map(p -> String.format("[%s] %s", fmtRange(p.getStartedAt(), p.getEndedAt()), p.getTitle()))
                    .collect(Collectors.joining(", "));

            StringBuilder electiveLines = new StringBuilder();
            for (int i = 0; i < elective.size(); i++) {
                TrainingProgram p = elective.get(i);
                electiveLines.append(String.format(
                        "*선택 %d: [%s] %s%n",
                        i + 1,
                        fmtRange(p.getStartedAt(), p.getEndedAt()),
                        p.getTitle()
                ));
            }

            String smsText = String.format(
                    "선생님은 (%s) 연수를 신청하셨습니다.%n%n" +
                            "*공통 : %s%n" +
                            "%s%n" +
                            "\n*이수 조건 : 신청 시수의 80%% 이상 수강%n" +
                            "*알찬 연수로 2025 AI광주미래교육 박람회장에서 선생님을 기다리겠습니다. (문의:380-4587)",
                    giPart, commonLine, electiveLines.toString().trim()
            );

            Message message = new Message();
            message.setFrom(smsProperties.getFromTraineeNumber());
            message.setTo(event.getPhoneNumber());
            message.setText(smsText);
            messageService.sendOne(new SingleMessageSendingRequest(message));

        } catch (RequiredKeynoteOrTeacherMissingException | InvalidTrainingSectionException e) {
            log.warn("[TrainingSms] business rule violation: {}", e.toString());
        } catch (Exception e) {
            log.error("[TrainingSms] unexpected error", e);
        }
    }

    private static Comparator<TrainingProgram> byStartDateTimeThenTitle() {
        return Comparator
                .comparing(TrainingSmsEventHandler::parseDateTimeSafe, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(TrainingProgram::getTitle, Comparator.nullsLast(String::compareTo));
    }

    private static LocalDateTime parseDateTimeSafe(TrainingProgram p) {
        LocalDateTime ldt = parseDateTime(p.getStartedAt());
        if (ldt != null) return ldt;
        LocalDate d = parseDate(p.getStartedAt());
        LocalTime t = parseTime(p.getStartedAt());
        if (d != null && t != null) return LocalDateTime.of(d, t);
        if (d != null) return LocalDateTime.of(d, LocalTime.MIDNIGHT);
        if (t != null) {
            LocalDate d2 = parseDate(p.getEndedAt());
            if (d2 != null) return LocalDateTime.of(d2, t);
            return LocalDateTime.of(LocalDate.of(1970,1,1), t);
        }
        return null;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static String fmtRange(String startStr, String endStr) {
        LocalTime a = parseTime(startStr);
        LocalTime b = parseTime(endStr);
        if (a == null && b == null) return "--:--~--:--";
        if (a == null) return "--:--~" + TIME_FMT.format(b);
        if (b == null) return TIME_FMT.format(a) + "~--:--";
        LocalTime s = a.isBefore(b) ? a : b;
        LocalTime e = a.isBefore(b) ? b : a;
        return TIME_FMT.format(s) + "~" + TIME_FMT.format(e);
    }

    private static LocalTime parseTime(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim();
        try {
            LocalDateTime ldt = LocalDateTime.parse(t, java.time.format.DateTimeFormatter.ISO_DATE_TIME);
            return ldt.toLocalTime();
        } catch (DateTimeParseException ignored) {}
        if (t.matches("^\\d{4}$")) {
            String norm = t.substring(0, 2) + ":" + t.substring(2);
            try { return LocalTime.parse(norm, DateTimeFormatter.ofPattern("HH:mm")); }
            catch (DateTimeParseException ignored) {}
        }
        try { return LocalTime.parse(t, DateTimeFormatter.ofPattern("H:mm")); }
        catch (DateTimeParseException ignored) {}
        try { return LocalTime.parse(t, DateTimeFormatter.ofPattern("HH:mm")); }
        catch (DateTimeParseException ignored) {}
        var m = java.util.regex.Pattern.compile("(?:T|\\s)?(\\d{1,2}):(\\d{2})").matcher(t);
        if (m.find()) {
            String hh = m.group(1);
            String mm = m.group(2);
            String norm = (hh.length() == 1 ? "0" + hh : hh) + ":" + mm;
            try { return LocalTime.parse(norm, DateTimeFormatter.ofPattern("HH:mm")); }
            catch (DateTimeParseException ignored) {}
        }
        m = java.util.regex.Pattern.compile("(\\d{1,2})\\s*시\\s*(\\d{1,2})?").matcher(t);
        if (m.find()) {
            String hh = m.group(1);
            String mm = m.group(2) == null ? "00" : m.group(2);
            String norm = (hh.length() == 1 ? "0" + hh : hh) + ":" + (mm.length() == 1 ? "0" + mm : mm);
            try { return LocalTime.parse(norm, DateTimeFormatter.ofPattern("HH:mm")); }
            catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    private static LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim();
        try {
            return LocalDateTime.parse(t, java.time.format.DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException ignored) {}
        return null;
    }

    private static boolean isOnDay(TrainingProgram p, int day) {
        LocalDate d = parseDate(p.getStartedAt());
        if (d != null && d.getDayOfMonth() == day) return true;
        d = parseDate(p.getEndedAt());
        return d != null && d.getDayOfMonth() == day;
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim();
        try { return LocalDate.parse(t, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE); }
        catch (DateTimeParseException ignored) {}
        try {
            LocalDateTime ldt = LocalDateTime.parse(t, java.time.format.DateTimeFormatter.ISO_DATE_TIME);
            return ldt.toLocalDate();
        } catch (DateTimeParseException ignored) {}
        var m = java.util.regex.Pattern.compile("(\\d{4})[-/.](\\d{2})[-/.](\\d{2})").matcher(t);
        if (m.find()) {
            String norm = m.group(1) + "-" + m.group(2) + "-" + m.group(3);
            try { return LocalDate.parse(norm, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE); }
            catch (DateTimeParseException ignored) {}
        }
        return null;
    }
}