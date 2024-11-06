package team.startup.expo.domain.expo.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.Admin;
import team.startup.expo.domain.admin.util.UserUtil;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.presentation.dto.request.GenerateExpoRequestDto;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.expo.service.GenerateExpoService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class GenerateExpoServiceImpl implements GenerateExpoService {

    private final ExpoRepository expoRepository;
    private final UserUtil userUtil;

    public void execute(GenerateExpoRequestDto dto) {
        Admin admin = userUtil.getCurrentUser();

        saveExpo(dto, admin);
    }

    private void saveExpo(GenerateExpoRequestDto dto, Admin admin) {
        Expo expo = Expo.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startedDay(dto.getStartedDay())
                .finishedDay(dto.getFinishedDay())
                .location(dto.getLocation())
                .coverImage(dto.getCoverImage())
                .x(dto.getX())
                .y(dto.getY())
                .admin(admin)
                .build();

        expoRepository.save(expo);
    }
}
