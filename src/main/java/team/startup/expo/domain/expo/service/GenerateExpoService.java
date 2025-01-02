package team.startup.expo.domain.expo.service;

import team.startup.expo.domain.expo.presentation.dto.request.GenerateExpoRequestDto;
import team.startup.expo.domain.expo.presentation.dto.response.GenerateExpoResponseDto;

public interface GenerateExpoService {
    GenerateExpoResponseDto execute(GenerateExpoRequestDto dto);
}
