package team.startup.expo.domain.form.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.form.entity.FormType;
import team.startup.expo.domain.form.entity.ParticipationType;

import java.util.List;

@Getter
@NoArgsConstructor
public class FormRequestDto {
    @NotNull
    private String informationImage;

    @NotNull
    private ParticipationType participantType;

    @NotNull
    private List<CreateDynamicFormRequestDto> dynamicForm;

    @Getter
    @NoArgsConstructor
    public static class CreateDynamicFormRequestDto {
        @NotNull
        private String title;

        @NotNull
        private FormType formType;

        @NotNull
        private String jsonData;
    }
}
