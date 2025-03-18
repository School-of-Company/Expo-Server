package team.startup.expo.domain.expo.service;

import team.startup.expo.domain.expo.presentation.dto.response.GetExpoValidationStatusResponseDto;

public interface GetExpoValidationStatusService {
    GetExpoValidationStatusResponseDto execute(String expoId);
}
