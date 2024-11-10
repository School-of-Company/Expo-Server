package team.startup.expo.domain.expo.service;

import team.startup.expo.domain.expo.presentation.dto.response.GetExpoInfoResponseDto;

public interface GetExpoInfoService {
    GetExpoInfoResponseDto execute(Long expoId);
}
