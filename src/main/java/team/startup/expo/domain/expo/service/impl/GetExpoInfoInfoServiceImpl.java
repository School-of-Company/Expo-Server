package team.startup.expo.domain.expo.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.presentation.dto.response.GetExpoInfoResponseDto;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.expo.service.GetExpoInfoService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetExpoInfoInfoServiceImpl implements GetExpoInfoService {

    private final ExpoRepository expoRepository;

    public GetExpoInfoResponseDto execute(Long expoId) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        return GetExpoInfoResponseDto.builder()
                .title(expo.getTitle())
                .description(expo.getDescription())
                .startedDay(expo.getStartedDay())
                .finishedDay(expo.getFinishedDay())
                .location(expo.getLocation())
                .coverImage(expo.getCoverImage())
                .x(expo.getX())
                .y(expo.getY())
                .build();
    }
}
