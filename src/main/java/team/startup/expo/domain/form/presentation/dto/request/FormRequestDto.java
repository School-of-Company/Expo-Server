package team.startup.expo.domain.form.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.form.entity.DynamicFormType;
import team.startup.expo.domain.form.entity.FormType;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.trainee.entity.ApplicationType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class FormRequestDto {
    @NotNull
    private String title;

    private String informationText;

    @NotNull
    private ParticipationType participantType;

    @Valid
    @NotNull
    private List<DynamicFormRequestDto> dynamicForm;

    @NotNull
    private ApplicationType applicationType;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @Getter
    @NoArgsConstructor
    public static class DynamicFormRequestDto {
        @NotNull
        private String title;

        @NotNull
        private FormType formType;

        @NotNull
        private String jsonData;

        @NotNull
        private Boolean requiredStatus;

        private String otherJson;

        @NotNull
        private DynamicFormType dynamicFormType;
    }
}
