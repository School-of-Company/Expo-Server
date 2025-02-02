package team.startup.expo.domain.survey.answer.service;

import team.startup.expo.domain.survey.answer.presentation.dto.request.StandardSurveyAnswerRequestDto;

public interface StandardSurveyAnswerService {
    void execute(String expoId, StandardSurveyAnswerRequestDto dto);
}
