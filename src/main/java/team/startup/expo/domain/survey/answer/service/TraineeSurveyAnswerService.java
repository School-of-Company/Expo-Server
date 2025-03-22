package team.startup.expo.domain.survey.answer.service;

import team.startup.expo.domain.survey.answer.presentation.dto.request.SurveyAnswerRequestDto;

public interface TraineeSurveyAnswerService {
    void execute(String expoId, SurveyAnswerRequestDto dto);
}
