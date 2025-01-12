package team.startup.expo.domain.training.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.application.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.sms.exception.NotFoundTraineeException;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.training.entity.TrainingProgram;
import team.startup.expo.domain.training.entity.TrainingProgramUser;
import team.startup.expo.domain.training.exception.NotFoundTrainingProgramException;
import team.startup.expo.domain.training.presentation.dto.request.ApplicationTrainingProListRequestDto;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.domain.training.service.ApplicationTrainingProListService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class ApplicationTrainingProListServiceImpl implements ApplicationTrainingProListService {

    private final TraineeRepository traineeRepository;
    private final TrainingProgramUserRepository trainingProgramUserRepository;
    private final TrainingProgramRepository trainingProgramRepository;

    public void execute(ApplicationTrainingProListRequestDto dto) {
        Trainee trainee = traineeRepository.findByTrainingId(dto.getTrainingId())
                .orElseThrow(NotFoundTraineeException::new);

        dto.getTrainingProIds().forEach(trainingProId -> {saveTrainingProUser(trainee, trainingProId);});
    }

    private void saveTrainingProUser(Trainee trainee, Long trainingProId) {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingProId)
                .orElseThrow(NotFoundTrainingProgramException::new);

        if (trainingProgramUserRepository.existsByTraineeAndTrainingProgram(trainee, trainingProgram))
            throw new AlreadyApplicationUserException();

        TrainingProgramUser trainingProgramUser = TrainingProgramUser.builder()
                .trainingProgram(trainingProgram)
                .trainee(trainee)
                .status(false)
                .build();

        trainingProgramUserRepository.save(trainingProgramUser);
    }
}
