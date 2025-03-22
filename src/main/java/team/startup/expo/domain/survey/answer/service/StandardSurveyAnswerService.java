package team.startup.expo.domain.survey.answer.service;

import team.startup.expo.domain.survey.answer.presentation.dto.request.SurveyAnswerRequestDto;

public interface StandardSurveyAnswerService {
    void execute(String expoId, SurveyAnswerRequestDto dto);
}
