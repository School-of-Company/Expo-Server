package team.startup.expo.domain.training.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.training.TrainingProgram;
import team.startup.expo.domain.training.exception.NotFoundTrainingProgramException;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.domain.training.service.DeleteTrainingProService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class DeleteTrainingProServiceImpl implements DeleteTrainingProService {

    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingProgramUserRepository trainingProgramUserRepository;

    public void execute(Long trainingProId) {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingProId)
                .orElseThrow(NotFoundTrainingProgramException::new);

        trainingProgramUserRepository.deleteByTrainingProgram(trainingProgram);
        trainingProgramRepository.delete(trainingProgram);
    }
}
