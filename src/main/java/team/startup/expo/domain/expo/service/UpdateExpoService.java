package team.startup.expo.domain.expo.service;

import team.startup.expo.domain.expo.presentation.dto.request.UpdateExpoRequestDto;

public interface UpdateExpoService {
    void execute(String expoId, UpdateExpoRequestDto dto);
}
