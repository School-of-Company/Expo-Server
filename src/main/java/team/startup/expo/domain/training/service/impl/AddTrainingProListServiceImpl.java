package team.startup.expo.domain.training.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.training.TrainingProgram;
import team.startup.expo.domain.training.presentation.dto.request.AddTrainingProRequestDto;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.domain.training.service.AddTrainingProListService;
import team.startup.expo.global.annotation.TransactionService;

import java.util.List;

@TransactionService
@RequiredArgsConstructor
public class AddTrainingProListServiceImpl implements AddTrainingProListService {

    private final TrainingProgramRepository trainingProgramRepository;
    private final ExpoRepository expoRepository;

    public void execute(Long expoId, List<AddTrainingProRequestDto> dtos) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        dtos.forEach(dto -> saveTrainingProgram(dto, expo));
    }

    private void saveTrainingProgram(AddTrainingProRequestDto dto, Expo expo) {
        TrainingProgram trainingProgram = TrainingProgram.builder()
                .title(dto.getTitle())
                .startedAt(String.valueOf(dto.getStartedAt()))
                .endedAt(String.valueOf(dto.getEndedAt()))
                .category(dto.getCategory())
                .expo(expo)
                .build();

        trainingProgramRepository.save(trainingProgram);
    }
}
