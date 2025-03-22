package team.startup.expo.domain.training.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.training.entity.TrainingProgram;
import team.startup.expo.domain.training.exception.NotFoundTrainingProgramException;
import team.startup.expo.domain.training.presentation.dto.request.UpdateTrainingProRequestDto;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.domain.training.service.UpdateTrainingProService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class UpdateTrainingProServiceImpl implements UpdateTrainingProService {

    private final TrainingProgramRepository trainingProgramRepository;

    public void execute(Long trainingProId, UpdateTrainingProRequestDto dto) {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingProId)
                .orElseThrow(NotFoundTrainingProgramException::new);

        trainingProgramRepository.save(dto.toEntity(trainingProgram));
    }
}
