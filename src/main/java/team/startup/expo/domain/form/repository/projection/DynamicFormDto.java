package team.startup.expo.domain.form.repository.projection;

import team.startup.expo.domain.form.entity.FormType;

public record DynamicFormDto(
        Long id,
        FormType formType,
        String title,
        String jsonData,
        Boolean requiredStatus,
        String otherJson,
        Long formId
) {
}
