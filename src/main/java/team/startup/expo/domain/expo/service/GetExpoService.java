package team.startup.expo.domain.expo.service;

import team.startup.expo.domain.expo.presentation.dto.response.GetExpoResponseDto;

public interface GetExpoService {
    GetExpoResponseDto execute(Long expoId);
}
