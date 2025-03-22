package team.startup.expo.domain.application.service;

import team.startup.expo.domain.application.presentation.dto.request.ApplicationTemporaryQrRequestDto;
import team.startup.expo.domain.application.presentation.dto.response.ApplicationTemporaryQrResponseDto;

public interface FieldApplicationTemporaryQrService {
    ApplicationTemporaryQrResponseDto execute(String expoId, ApplicationTemporaryQrRequestDto dto);
}
