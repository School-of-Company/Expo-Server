package team.startup.expo.domain.survey.management.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.form.entity.FormType;
import team.startup.expo.domain.form.entity.ParticipationType;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class SurveyRequestDto {
    @NotNull
    private ParticipationType participationType;

    @NotNull
    private String informationText;

    @Valid
    @NotNull
    private List<DynamicSurveyRequestDto> dynamicSurveyRequestDto;

    @Getter
    @NoArgsConstructor
    public static class DynamicSurveyRequestDto {
        @NotNull
        private String title;

        @NotNull
        private Map<String, Object> jsonData;

        @NotNull
        private FormType formType;

        @NotNull
        private Boolean requiredStatus;

        private Map<String, Object> otherJson;
    }
}
