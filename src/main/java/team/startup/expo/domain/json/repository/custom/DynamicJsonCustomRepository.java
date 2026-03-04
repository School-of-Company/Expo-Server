package team.startup.expo.domain.json.repository.custom;

public interface DynamicJsonCustomRepository {
    void deleteAllByFormId(Long formId);
    void deleteAllBySurveyId(Long surveyId);
}
