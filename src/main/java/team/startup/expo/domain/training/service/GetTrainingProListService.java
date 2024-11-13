package team.startup.expo.domain.training.service;

import team.startup.expo.domain.training.presentation.dto.response.GetTrainingProResponse;

import java.util.List;

public interface GetTrainingProListService {
    List<GetTrainingProResponse> execute(Long expoId);
}
