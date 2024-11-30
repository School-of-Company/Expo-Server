package team.startup.expo.domain.training.service;

import team.startup.expo.domain.training.presentation.dto.request.ApplicationTrainingProRequestDto;

public interface ApplicationTrainingProService {
    void execute(Long trainingProId, ApplicationTrainingProRequestDto dto);
}
