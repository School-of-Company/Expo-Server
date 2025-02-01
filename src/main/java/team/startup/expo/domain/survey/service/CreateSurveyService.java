package team.startup.expo.domain.survey.service;

import team.startup.expo.domain.survey.presentation.dto.request.SurveyRequestDto;

public interface CreateSurveyService {
    void execute(String expoId, SurveyRequestDto dto);
}
