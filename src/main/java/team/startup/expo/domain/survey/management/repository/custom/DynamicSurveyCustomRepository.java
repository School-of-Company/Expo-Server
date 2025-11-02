package team.startup.expo.domain.survey.management.repository.custom;

import java.util.List;

public interface DynamicSurveyCustomRepository {
    List<Long> findIdsBySurveyId(Long surveyId);
    long deleteBySurveyId(Long surveyId);
}
