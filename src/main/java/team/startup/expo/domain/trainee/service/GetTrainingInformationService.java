package team.startup.expo.domain.trainee.service;

import team.startup.expo.domain.trainee.presentation.dto.response.GetTraineeInformationResponseDto;

import java.util.List;

public interface GetTrainingInformationService {
    List<GetTraineeInformationResponseDto> execute(String expoId, String name);
}
