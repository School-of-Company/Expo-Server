package team.startup.expo.domain.survey.management.repository.projection;

import team.startup.expo.domain.form.entity.FormType;

public record DynamicSurveyDto(
        Long id,
        FormType formType,
        String title,
        String jsonData,
        Boolean requiredStatus,
        String otherJson,
        Long surveyId
) {
}
