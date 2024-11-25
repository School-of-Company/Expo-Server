package team.startup.expo.domain.expo.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.presentation.dto.response.GetExpoResponseDto;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.expo.service.GetExpoListService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;
import team.startup.expo.global.common.ulid.ULIDGenerator;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetExpoListServiceImpl implements GetExpoListService {

    private final ExpoRepository expoRepository;
    private final ULIDGenerator ulidGenerator;

    public List<GetExpoResponseDto> execute() {
        return expoRepository.findAll().stream()
                .map(expo -> GetExpoResponseDto.builder()
                        .id(expo.getId())
                        .title(expo.getTitle())
                        .description(expo.getDescription())
                        .startedDay(expo.getStartedDay())
                        .finishedDay(expo.getFinishedDay())
                        .coverImage(expo.getCoverImage())
                        .build())
                .toList();
    }
}
