package team.startup.expo.domain.training.service;

import team.startup.expo.domain.training.presentation.dto.request.ApplicationTrainingProListAndTraineeRequestDto;

public interface ApplicationTrainingProListAndTraineeService {
    void execute(String expoId, ApplicationTrainingProListAndTraineeRequestDto request);
}
