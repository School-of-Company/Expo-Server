package team.startup.expo.domain.form.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.form.entity.FormType;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.trainee.entity.ApplicationType;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class FormRequestDto {
    private String informationText;

    @NotNull
    private ParticipationType participantType;

    @Valid
    @NotNull
    private List<DynamicFormRequestDto> dynamicForm;

    @NotNull
    private ApplicationType applicationType;

    @Getter
    @NoArgsConstructor
    public static class DynamicFormRequestDto {
        @NotNull
        private String title;

        @NotNull
        private FormType formType;

        @NotNull
        private Map<String, Object> jsonData;

        @NotNull
        private Boolean requiredStatus;

        private Map<String, Object> otherJson;
    }
}
