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

        List<StandardProgram> standardPrograms = dto.getAddStandardProRequestDto().stream()
                .map(req -> StandardProgram.builder()
                        .title(req.getTitle())
                        .startedAt(String.valueOf(req.getStartedAt()))
                        .endedAt(String.valueOf(req.getEndedAt()))
                        .expo(expo)
                        .build())
                .toList();
        standardProgramRepository.saveAll(standardPrograms);

        List<TrainingProgram> trainingPrograms = dto.getAddTrainingProRequestDto().stream()
                .map(req -> TrainingProgram.builder()
                        .title(req.getTitle())
                        .startedAt(String.valueOf(req.getStartedAt()))
                        .endedAt(String.valueOf(req.getEndedAt()))
                        .category(req.getCategory())
                        .expo(expo)
                        .build())
                .toList();
        trainingProgramRepository.saveAll(trainingPrograms);

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

}
