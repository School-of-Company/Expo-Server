package team.startup.expo.domain.training.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import team.startup.expo.domain.application.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.sms.exception.NotFoundTraineeException;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.training.entity.TrainingProgram;
import team.startup.expo.domain.training.entity.TrainingProgramUser;
import team.startup.expo.domain.training.event.TrainingSmsEvent;
import team.startup.expo.domain.training.presentation.dto.request.ApplicationTrainingProListRequestDto;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.domain.training.service.ApplicationTrainingProListService;
import team.startup.expo.global.annotation.TransactionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@TransactionService
@RequiredArgsConstructor
public class ApplicationTrainingProListServiceImpl implements ApplicationTrainingProListService {

    private final TraineeRepository traineeRepository;
    private final TrainingProgramUserRepository trainingProgramUserRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void execute(ApplicationTrainingProListRequestDto dto) {
        Trainee trainee = traineeRepository.findByTrainingId(dto.getTrainingId())
                .orElseThrow(NotFoundTraineeException::new);

        List<TrainingProgram> trainingProgramList = trainingProgramRepository.findAllByIdIn(dto.getTrainingProIds());
        if (trainingProgramUserRepository.existsByTraineeIdAndIdIn(trainee.getId(), dto.getTrainingProIds())) {
            throw new AlreadyApplicationUserException();
        }

        trainingProgramList.forEach(trainingProgram -> {saveTrainingProUser(trainingProgram, trainee);});

        applicationEventPublisher.publishEvent(new TrainingSmsEvent(trainee.getExpo().getId(), trainee.getPhoneNumber(), trainingProgramList));
    }

    private void saveTrainingProUser(TrainingProgram trainingProgram, Trainee trainee) {
        trainingProgramUserRepository.save(TrainingProgramUser.builder()
                .attendanceDate(parseToLocalDate(trainingProgram.getStartedAt()))
                .trainingProgram(trainingProgram)
                .trainee(trainee)
                .status(false)
                .build());
    }

    private LocalDate parseToLocalDate(String startedAt) {
        if (startedAt == null || startedAt.isBlank()) {
            throw new IllegalArgumentException("startedAt이 비어있습니다.");
        }
        String s = startedAt.trim();

        // "yyyy-MM-dd HH:mm" 같은 공백 구분을 ISO_LOCAL_DATE_TIME 형태로 보정
        if (s.matches("^\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}(:\\d{2})?$")) {
            s = s.replace(' ', 'T');
        }

        // 1) 날짜만
        try { return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE); }
        catch (DateTimeParseException ignored) {}

        // 2) 로컬 날짜시간
        try { return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate(); }
        catch (DateTimeParseException ignored) {}

        // 3) 오프셋 포함
        try { return OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate(); }
        catch (DateTimeParseException ignored) {}

        // 4) 존 포함
        try { return ZonedDateTime.parse(s, DateTimeFormatter.ISO_ZONED_DATE_TIME).toLocalDate(); }
        catch (DateTimeParseException e) {
            throw new IllegalArgumentException("startedAt 포맷이 올바르지 않습니다: " + startedAt);
        }
    }
}
}
