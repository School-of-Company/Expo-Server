package team.startup.expo.domain.expo.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.entity.Admin;
import team.startup.expo.domain.admin.util.UserUtil;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.presentation.dto.request.GenerateExpoRequestDto;
import team.startup.expo.domain.expo.presentation.dto.response.GenerateExpoResponseDto;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.expo.service.GenerateExpoService;
import team.startup.expo.domain.standard.entity.StandardProgram;
import team.startup.expo.domain.standard.presentation.dto.request.AddStandardProRequestDto;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.training.entity.TrainingProgram;
import team.startup.expo.domain.training.presentation.dto.request.AddTrainingProRequestDto;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.common.ulid.ULIDGenerator;

import java.util.List;

@TransactionService
@RequiredArgsConstructor
public class GenerateExpoServiceImpl implements GenerateExpoService {

    private final ExpoRepository expoRepository;
    private final StandardProgramRepository standardProgramRepository;
    private final TrainingProgramRepository trainingProgramRepository;

    public GenerateExpoResponseDto execute(GenerateExpoRequestDto dto) {
        Expo expo = saveExpo(dto);

        dto.getAddStandardProRequestDto().forEach(addStandardProRequestDto -> {saveStandardPro(addStandardProRequestDto, expo);});
        dto.getAddTrainingProRequestDto().forEach(addTrainingProRequestDto -> {saveTrainingPro(addTrainingProRequestDto, expo);});

        return GenerateExpoResponseDto.builder()
                .expoId(expo.getId())
                .build();
    }

    private Expo saveExpo(GenerateExpoRequestDto dto) {
        Expo expo = Expo.builder()
                .id(ULIDGenerator.generateULID())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startedDay(String.valueOf(dto.getStartedDay()))
                .finishedDay(String.valueOf(dto.getFinishedDay()))
                .location(dto.getLocation())
                .coverImage(dto.getCoverImage())
                .x(dto.getX())
                .y(dto.getY())
                .applicationPerson(0L)
                .yesterdayApplicationPerson(0L)
                .build();

        expoRepository.save(expo);

        return expo;
    }

    private void saveStandardPro(AddStandardProRequestDto dto, Expo expo) {
        StandardProgram standardProgram = StandardProgram.builder()
                .title(dto.getTitle())
                .startedAt(String.valueOf(dto.getStartedAt()))
                .endedAt(String.valueOf(dto.getEndedAt()))
                .expo(expo)
                .build();

        standardProgramRepository.save(standardProgram);
    }

    private void saveTrainingPro(AddTrainingProRequestDto dto, Expo expo) {
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
