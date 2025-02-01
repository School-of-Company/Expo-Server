package team.startup.expo.domain.survey.service;

import team.startup.expo.domain.survey.presentation.dto.request.SurveyRequestDto;

public interface UpdateSurveyService {
    void execute(String expoId, SurveyRequestDto dto);
}
