package team.startup.expo.domain.survey.management.repository.custom;

import team.startup.expo.domain.survey.management.repository.projection.DynamicSurveyDto;

import java.util.List;

public interface DynamicSurveyCustomRepository {
    long deleteBySurveyId(Long surveyId);
    List<DynamicSurveyDto> findAllBySurveyIdWithJson(Long surveyId);
}
