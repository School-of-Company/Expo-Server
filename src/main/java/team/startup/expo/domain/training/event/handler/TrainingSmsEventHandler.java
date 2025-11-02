package team.startup.expo.domain.training.event.handler;

import lombok.RequiredArgsConstructor;
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
import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;
import team.startup.expo.global.sms.SmsProperties;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

            List<TrainingProgram> common = programs.stream()
                    .filter(p -> containsAny(p.getTitle(), "강연", "교사"))
                    .collect(Collectors.toList());

            List<TrainingProgram> electiveAll = programs.stream()
                    .filter(p -> !containsAny(p.getTitle(), "강연", "교사"))
                    .collect(Collectors.toList());

            boolean hasKeynote = programs.stream()
                    .anyMatch(p -> containsAny(p.getTitle(), "기조 강연"));
            int electiveLimit = hasKeynote ? 2 : 4;

            if (common.isEmpty()) {
                throw new RequiredKeynoteOrTeacherMissingException();
            }

            if (electiveAll.size() > electiveLimit) {
                throw new InvalidTrainingSectionException();
            }

            List<TrainingProgram> elective = electiveAll.stream()
                    .limit(electiveLimit)
                    .collect(Collectors.toList());

            List<TrainingProgram> included = new ArrayList<>();
            included.addAll(common);
            included.addAll(elective);

            long electiveCount = elective.size();
            long specialCount = included.stream()
                    .filter(p -> containsAny(p.getTitle(), "특별"))
                    .count();
            long teacherCount = included.stream()
                    .filter(p -> containsAny(p.getTitle(), "교사"))
                    .count();

            int gi = 0;
            if (electiveCount == 1) gi = Math.max(gi, 1);
            else if (electiveCount >= 2) gi = Math.max(gi, 2);
            if (specialCount >= 1 && specialCount <= 4) gi = Math.max(gi, (int) (2 + specialCount));
            else if (specialCount > 4) gi = Math.max(gi, 6);
            if (teacherCount >= 1 && teacherCount <= 4) gi = Math.max(gi, (int) (6 + teacherCount));
            else if (teacherCount > 4) gi = Math.max(gi, 10);

            int totalHours = included.stream()
                    .mapToInt(p -> hoursBetween(p.getStartedAt(), p.getEndedAt()))
                    .sum();

            String commonLine = common.isEmpty()
                    ? "없음"
                    : common.stream()
                    .map(p -> String.format("(%s~%s) %s",
                            fmt(p.getStartedAt()),
                            fmt(p.getEndedAt()),
                            p.getTitle()))
                    .collect(Collectors.joining(", "));

            StringBuilder electiveLines = new StringBuilder();
            for (int i = 0; i < elective.size(); i++) {
                TrainingProgram p = elective.get(i);
                electiveLines.append(String.format(
                        "*선택 %d: (%s~%s) %s%n",
                        i + 1,
                        fmt(p.getStartedAt()),
                        fmt(p.getEndedAt()),
                        p.getTitle()
                ));
            }

            String smsText = String.format(
                    "선생님은 (%d기(%d시간)) 연수를 신청하셨습니다.%n" +
                            "*공통 : %s%n" +
                            "%s%n" +
                            "*이수 조건 : 신청 시수의 80%% 이상 수강%n" +
                            "*알찬 연수로 2025 AI광주미래교육 박람회장에서 선생님을 기다리겠습니다. (문의:380-4587)",
                    gi, totalHours + 1, commonLine, electiveLines.toString().trim()
            );

            Message message = createMessage(event, smsText);
            messageService.sendOne(new SingleMessageSendingRequest(message));
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private static boolean containsAny(String target, String... keywords) {
        if (target == null) return false;
        for (String k : keywords) {
            if (k != null && target.contains(k)) return true;
        }
        return false;
    }

    private static String fmt(String timeStr) {
        LocalTime t = parseTime(timeStr);
        return t == null ? "--:--" : TIME_FMT.format(t);
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
        try { return LocalTime.parse(trimmed, DateTimeFormatter.ofPattern("H:mm")); }
        catch (DateTimeParseException ignored) {}
        try { return LocalTime.parse(trimmed, DateTimeFormatter.ofPattern("HH:mm")); }
        catch (DateTimeParseException ignored) {}
        if (trimmed.matches("^\\d{4}$")) {
            String norm = trimmed.substring(0, 2) + ":" + trimmed.substring(2);
            try { return LocalTime.parse(norm, DateTimeFormatter.ofPattern("HH:mm")); }
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