package team.startup.expo.domain.expo.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.util.UserUtil;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.presentation.dto.request.UpdateExpoRequestDto;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.expo.service.UpdateExpoService;
import team.startup.expo.domain.standard.entity.StandardProgram;
import team.startup.expo.domain.standard.presentation.dto.request.UpdateStandardProRequestDto;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.training.entity.TrainingProgram;
import team.startup.expo.domain.training.presentation.dto.request.UpdateTrainingProRequestDto;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class UpdateExpoServiceImpl implements UpdateExpoService {

    private final ExpoRepository expoRepository;
    private final StandardProgramRepository standardProgramRepository;
    private final TrainingProgramRepository trainingProgramRepository;

    public void execute(String expoId, UpdateExpoRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        standardProgramRepository.deleteByExpo(expo);
        trainingProgramRepository.deleteByExpo(expo);

        dto.getUpdateStandardProRequestDto().forEach(standardProRequestDto -> saveStandardPro(standardProRequestDto, expo));
        dto.getUpdateTrainingProRequestDto().forEach(trainingProRequestDto -> saveTrainingPro(trainingProRequestDto, expo));

        expoRepository.save(dto.toEntity(expo));
    }

    private void saveStandardPro(UpdateStandardProRequestDto dto, Expo expo) {
        StandardProgram standardProgram = StandardProgram.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .startedAt(String.valueOf(dto.getStartedAt()))
                .endedAt(String.valueOf(dto.getEndedAt()))
                .expo(expo)
                .build();

        standardProgramRepository.save(standardProgram);
    }

    private void saveTrainingPro(UpdateTrainingProRequestDto dto, Expo expo) {
        TrainingProgram trainingProgram = TrainingProgram.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .startedAt(String.valueOf(dto.getStartedAt()))
                .endedAt(String.valueOf(dto.getEndedAt()))
                .category(dto.getCategory())
                .expo(expo)
                .build();

        trainingProgramRepository.save(trainingProgram);
    }
}
