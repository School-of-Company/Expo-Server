package team.startup.expo.domain.training.service;

import team.startup.expo.domain.training.presentation.dto.request.AddTrainingProRequestDto;

import java.util.List;

public interface AddTrainingProListService {
    void execute(Long expoId, List<AddTrainingProRequestDto> dtos);
}