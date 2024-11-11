package team.startup.expo.domain.expo.service;

import team.startup.expo.domain.expo.presentation.dto.response.GetExpoResponseDto;

import java.util.List;

public interface GetExpoListService {
    List<GetExpoResponseDto> execute();
}
