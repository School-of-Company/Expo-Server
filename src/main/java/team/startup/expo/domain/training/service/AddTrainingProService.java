package team.startup.expo.domain.training.service;

import team.startup.expo.domain.training.presentation.dto.request.AddTrainingProRequestDto;

public interface AddTrainingProService {
    void execute(Long expoId, AddTrainingProRequestDto dto);
}
