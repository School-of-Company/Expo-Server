package team.startup.expo.domain.expo.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.presentation.dto.response.GetExpoResponseDto;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.expo.service.GetExpoService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetExpoServiceImpl implements GetExpoService {

    private final ExpoRepository expoRepository;

    public GetExpoResponseDto execute(Long expoId) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        return GetExpoResponseDto.builder()
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
