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

        List<TrainingProgramUser> users = trainingProgramList.stream()
                .map(trainingProgram -> TrainingProgramUser.builder()
                        .attendanceDate(parseDate(trainingProgram.getStartedAt()))
                        .trainingProgram(trainingProgram)
                        .trainee(trainee)
                        .status(false)
                        .build())
                .toList();

        trainingProgramUserRepository.saveAll(users);

        applicationEventPublisher.publishEvent(new TrainingSmsEvent(trainee.getExpo().getId(), trainee.getPhoneNumber(), trainingProgramList));
    }

    private LocalDate parseDate(String dateTimeStr) {
        return LocalDate.parse(dateTimeStr.substring(0, 10)); // "yyyy-MM-dd"
    }
}
