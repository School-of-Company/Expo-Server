package team.startup.expo.domain.training.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.training.presentation.dto.response.GetTrainingProResponse;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.domain.training.service.GetTrainingProListService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetTrainingProListServiceImpl implements GetTrainingProListService {

    private final TrainingProgramRepository trainingProgramRepository;
    private final ExpoRepository expoRepository;

    public List<GetTrainingProResponse> execute(Long expoId) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        return trainingProgramRepository.findByExpo(expo).stream()
                .map(trainingProgram -> GetTrainingProResponse.builder()
                        .id(trainingProgram.getId())
                        .title(trainingProgram.getTitle())
                        .startedAt(trainingProgram.getStartedAt())
                        .endedAt(trainingProgram.getEndedAt())
                        .category(trainingProgram.getCategory())
                        .build())
                .toList();
    }
}
