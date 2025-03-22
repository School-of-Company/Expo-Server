package team.startup.expo.domain.application.service;

import team.startup.expo.domain.application.presentation.dto.request.ApplicationForTraineeRequestDto;

public interface PreApplicationForTraineeService {
    void execute(String expoId, ApplicationForTraineeRequestDto dto);
}
