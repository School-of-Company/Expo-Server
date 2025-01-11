package team.startup.expo.domain.expo.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.entity.Admin;
import team.startup.expo.domain.admin.util.UserUtil;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.presentation.dto.request.GenerateExpoRequestDto;
import team.startup.expo.domain.expo.presentation.dto.response.GenerateExpoResponseDto;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.expo.service.GenerateExpoService;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.common.ulid.ULIDGenerator;

@TransactionService
@RequiredArgsConstructor
public class GenerateExpoServiceImpl implements GenerateExpoService {

    private final ExpoRepository expoRepository;
    private final UserUtil userUtil;

    public GenerateExpoResponseDto execute(GenerateExpoRequestDto dto) {
        Admin admin = userUtil.getCurrentUser();

        String expoId = saveExpo(dto, admin);

        return GenerateExpoResponseDto.builder()
                .expoId(expoId)
                .build();
    }

    private String saveExpo(GenerateExpoRequestDto dto, Admin admin) {
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
                .admin(admin)
                .build();

        expoRepository.save(expo);

        return expo.getId();
    }
}
