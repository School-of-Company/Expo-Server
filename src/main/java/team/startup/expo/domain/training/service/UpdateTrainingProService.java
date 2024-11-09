package team.startup.expo.domain.training.service;

import team.startup.expo.domain.training.presentation.dto.request.UpdateTrainingProRequestDto;

public interface UpdateTrainingProService {
    void execute(Long trainingProId, UpdateTrainingProRequestDto dto);
}
