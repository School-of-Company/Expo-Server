package team.startup.expo.domain.survey.management.service;

import team.startup.expo.domain.survey.management.presentation.dto.request.SurveyRequestDto;

public interface UpdateSurveyService {
    void execute(String expoId, SurveyRequestDto dto);
}
