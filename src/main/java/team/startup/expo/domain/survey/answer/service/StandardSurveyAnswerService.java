package team.startup.expo.domain.survey.answer.service;

import team.startup.expo.domain.survey.answer.presentation.dto.request.SurveyAnswerRequestDto;
import team.startup.expo.domain.survey.answer.presentation.dto.response.StandardSurveyAnswerResponseDto;

public interface StandardSurveyAnswerService {
    StandardSurveyAnswerResponseDto execute(String expoId, SurveyAnswerRequestDto dto);
}
