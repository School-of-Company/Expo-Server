package team.startup.expo.domain.training.service;

import team.startup.expo.domain.training.presentation.dto.request.AddTrainingProRequestDto;

public interface AddTrainingProService {
    void execute(String expoId, AddTrainingProRequestDto dto);
}
