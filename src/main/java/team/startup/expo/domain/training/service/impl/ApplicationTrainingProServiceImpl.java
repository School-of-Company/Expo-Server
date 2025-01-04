package team.startup.expo.domain.training.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.application.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.sms.exception.NotFoundTraineeException;
import team.startup.expo.domain.trainee.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.training.TrainingProgram;
import team.startup.expo.domain.training.TrainingProgramUser;
import team.startup.expo.domain.training.exception.NotFoundTrainingProgramException;
import team.startup.expo.domain.training.presentation.dto.request.ApplicationTrainingProRequestDto;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.domain.training.service.ApplicationTrainingProService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class ApplicationTrainingProServiceImpl implements ApplicationTrainingProService {

    private final TraineeRepository traineeRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingProgramUserRepository trainingProgramUserRepository;

    public void execute(Long trainingProId, ApplicationTrainingProRequestDto dto) {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingProId)
                .orElseThrow(NotFoundTrainingProgramException::new);

        Trainee trainee = traineeRepository.findByTrainingId(dto.getTrainingId())
                .orElseThrow(NotFoundTraineeException::new);

        if (trainingProgramUserRepository.existsByTraineeAndTrainingProgram(trainee, trainingProgram))
            throw new AlreadyApplicationUserException();

        saveTrainingProUser(trainingProgram, trainee);
    }

    private void saveTrainingProUser(TrainingProgram trainingProgram, Trainee trainee) {
        TrainingProgramUser trainingProgramUser = TrainingProgramUser.builder()
                .trainingProgram(trainingProgram)
                .trainee(trainee)
                .status(false)
                .build();

        trainingProgramUserRepository.save(trainingProgramUser);
    }
}
