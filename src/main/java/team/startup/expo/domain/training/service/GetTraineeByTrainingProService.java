package team.startup.expo.domain.training.service;

import team.startup.expo.domain.training.presentation.dto.response.GetTrainingProTraineeResponseDto;

import java.util.List;

public interface GetTraineeByTrainingProService {
    List<GetTrainingProTraineeResponseDto> execute(Long trainingProId);
}
