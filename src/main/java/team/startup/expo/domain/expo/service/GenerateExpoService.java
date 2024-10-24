package team.startup.expo.domain.expo.service;

import team.startup.expo.domain.expo.presentation.dto.request.GenerateExpoRequestDto;

public interface GenerateExpoService {
    void execute(GenerateExpoRequestDto dto);
}
