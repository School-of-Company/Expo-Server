package team.startup.expo.domain.form.service;

import team.startup.expo.domain.form.presentation.dto.request.PreApplicationForTraineeRequestDto;

public interface PreApplicationForTraineeService {
    void execute(String expoId, PreApplicationForTraineeRequestDto dto);
}
