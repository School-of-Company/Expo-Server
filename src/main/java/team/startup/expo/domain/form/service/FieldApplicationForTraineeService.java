package team.startup.expo.domain.form.service;

import team.startup.expo.domain.form.presentation.dto.request.ApplicationForTraineeRequestDto;

public interface FieldApplicationForTraineeService {
    void execute(String expoId, ApplicationForTraineeRequestDto dto);
}
