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
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
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
            Expo expo = expoRepository.findById(event.getExpoId())
                    .orElseThrow(NotFoundExpoException::new);

            List<TrainingProgram> programs = event.getTrainingPrograms();

            List<TrainingProgram> keynotes = programs.stream()
                    .filter(p -> containsAny(p.getTitle(), "기조", "기조 강연"))
                    .collect(Collectors.toList());
            List<TrainingProgram> specials = programs.stream()
                    .filter(p -> containsAny(p.getTitle(), "특별"))
                    .collect(Collectors.toList());
            List<TrainingProgram> relays = programs.stream()
                    .filter(p -> containsAny(p.getTitle(), "교사", "릴레이"))
                    .collect(Collectors.toList());

            List<TrainingProgram> common = new ArrayList<>();
            common.addAll(keynotes);
            common.addAll(specials);
            common.addAll(relays);

            List<TrainingProgram> electiveAll = programs.stream()
                    .filter(p -> !containsAny(p.getTitle(), "기조", "기조 강연", "특별", "교사", "릴레이"))
                    .collect(Collectors.toList());

            boolean hasKeynote = !keynotes.isEmpty();
            boolean hasSpecial = !specials.isEmpty();
            boolean hasRelay   = !relays.isEmpty();

            if (common.isEmpty()) throw new RequiredKeynoteOrTeacherMissingException();
            if (hasSpecial && hasRelay) throw new InvalidTrainingSectionException();

            List<TrainingProgram> elective21 = electiveAll.stream()
                    .filter(p -> isOnDay(p, 21))
                    .sorted(byStartTimeThenTitle())
                    .collect(Collectors.toList());
            List<TrainingProgram> elective22 = electiveAll.stream()
                    .filter(p -> isOnDay(p, 22))
                    .sorted(byStartTimeThenTitle())
                    .collect(Collectors.toList());

            if (hasKeynote && elective21.size() > 2) throw new InvalidTrainingSectionException();
            boolean has22Group = hasSpecial || hasRelay;
            if (has22Group && elective22.size() > 4) throw new InvalidTrainingSectionException();

            List<TrainingProgram> elective = new ArrayList<>();
            if (hasKeynote) elective.addAll(elective21.stream().limit(2).toList());
            else            elective.addAll(elective21);
            if (has22Group) elective.addAll(elective22.stream().limit(4).toList());
            else            elective.addAll(elective22);

            List<Integer> giNumbers = new ArrayList<>();
            int electiveCount21 = elective21.size();
            int electiveCount22 = elective22.size();

            if (hasKeynote && electiveCount21 > 0) {
                int idx = clamp(electiveCount21, 1, 2);
                giNumbers.add(0 + idx);
            }
            if (hasSpecial && electiveCount22 > 0) {
                int idx = clamp(electiveCount22, 1, 4);
                giNumbers.add(2 + idx);
            } else if (hasRelay && electiveCount22 > 0) {
                int idx = clamp(electiveCount22, 1, 4);
                giNumbers.add(6 + idx);
            }

            // 표시 문자열ㅌ
            String commonLine = common.isEmpty()
                    ? "없음"
                    : common.stream()
                    .sorted(byStartTimeThenTitle())
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

            int[] giHours = {2, 3, 2, 3, 4, 5, 2, 3, 4, 5};
            String giPart = giNumbers.stream()
                    .map(n -> {
                        int hours = (n >= 1 && n <= 10) ? giHours[n - 1] : 0;
                        return String.format("%d기(%d시간)", n, hours);
                    })
                    .collect(Collectors.joining(", "));

            String smsText = String.format(
                    "선생님은 (%s) 연수를 신청하셨습니다.%n\n" +
                            "*공통 : %s%n" +
                            "%s%n" +
                            "\n*이수 조건 : 신청 시수의 80%% 이상 수강%n" +
                            "*알찬 연수로 2025 AI광주미래교육 박람회장에서 선생님을 기다리겠습니다. (문의:380-4587)",
                    giPart, commonLine, electiveLines.toString().trim()
            );

            Message message = createMessage(event, smsText);
            messageService.sendOne(new SingleMessageSendingRequest(message));

        } catch (RequiredKeynoteOrTeacherMissingException | InvalidTrainingSectionException e) {
            log.warn("[TrainingSms] business rule violation: {}", e.toString());
        } catch (Exception e) {
            log.error("[TrainingSms] unexpected error", e);
        }
    }

    private static Comparator<TrainingProgram> byStartTimeThenTitle() {
        return Comparator
                .comparing((TrainingProgram p) -> parseTime(p.getStartedAt()), Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(TrainingProgram::getTitle, Comparator.nullsLast(String::compareTo));
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static boolean containsAny(String target, String... keywords) {
        if (target == null) return false;
        for (String k : keywords) {
            if (k != null && target.contains(k)) return true;
        }
        return false;
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

    private static int hoursBetween(String startStr, String endStr) {
        LocalTime start = parseTime(startStr);
        LocalTime end = parseTime(endStr);
        if (start == null || end == null) return 0;
        long minutes = Duration.between(start, end).toMinutes();
        if (minutes < 0) minutes += 24 * 60;
        return (int) ((minutes + 59) / 60);
    }

    private static LocalTime parseTime(String s) {
        if (s == null || s.isBlank()) return null;
        String trimmed = s.trim();

        try {
            LocalDateTime ldt = LocalDateTime.parse(trimmed, java.time.format.DateTimeFormatter.ISO_DATE_TIME);
            return ldt.toLocalTime();
        } catch (DateTimeParseException ignored) {}

        if (trimmed.matches("^\\d{4}$")) { // 0930 → 09:30
            String norm = trimmed.substring(0, 2) + ":" + trimmed.substring(2);
            try { return LocalTime.parse(norm, DateTimeFormatter.ofPattern("HH:mm")); }
            catch (DateTimeParseException ignored) {}
        }

        try { return LocalTime.parse(trimmed, DateTimeFormatter.ofPattern("H:mm")); }
        catch (DateTimeParseException ignored) {}
        try { return LocalTime.parse(trimmed, DateTimeFormatter.ofPattern("HH:mm")); }
        catch (DateTimeParseException ignored) {}

        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("(?:T|\\s)?(\\d{1,2}):(\\d{2})")
                .matcher(trimmed);
        if (m.find()) {
            String hh = m.group(1);
            String mm = m.group(2);
            String norm = (hh.length() == 1 ? "0" + hh : hh) + ":" + mm;
            try { return LocalTime.parse(norm, DateTimeFormatter.ofPattern("HH:mm")); }
            catch (DateTimeParseException ignored) {}
        }

        m = java.util.regex.Pattern
                .compile("(\\d{1,2})\\s*시\\s*(\\d{1,2})?\\s*분?")
                .matcher(trimmed);
        if (m.find()) {
            String hh = m.group(1);
            String mm = (m.group(2) == null) ? "00" : m.group(2);
            String norm = (hh.length() == 1 ? "0" + hh : hh) + ":" + (mm.length() == 1 ? "0" + mm : mm);
            try { return LocalTime.parse(norm, DateTimeFormatter.ofPattern("HH:mm")); }
            catch (DateTimeParseException ignored) {}
        }

        return null;
    }

    private static boolean isOnDay(TrainingProgram p, int dayOfMonth) {
        LocalDate d = extractDate(p);
        return d != null && d.getDayOfMonth() == dayOfMonth;
    }

    private static LocalDate extractDate(TrainingProgram p) {
        LocalDate d = parseDate(p.getStartedAt());
        if (d != null) return d;
        return parseDate(p.getEndedAt());
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        String trimmed = s.trim();

        try {
            return LocalDate.parse(trimmed, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {}

        try {
            LocalDateTime ldt = LocalDateTime.parse(trimmed, java.time.format.DateTimeFormatter.ISO_DATE_TIME);
            return ldt.toLocalDate();
        } catch (DateTimeParseException ignored) {}

        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("(\\d{4})[-/.](\\d{2})[-/.](\\d{2})")
                .matcher(trimmed);
        if (m.find()) {
            String norm = m.group(1) + "-" + m.group(2) + "-" + m.group(3);
            try { return LocalDate.parse(norm, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE); }
            catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    private Message createMessage(TrainingSmsEvent event, String information) {
        Message message = new Message();
        message.setFrom(smsProperties.getFromTraineeNumber());
        message.setTo(event.getPhoneNumber());
        message.setText(information);
        return message;
    }
}