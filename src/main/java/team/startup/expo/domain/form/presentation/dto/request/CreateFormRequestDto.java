package team.startup.expo.domain.form.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.form.entity.FormType;

@Getter
@NoArgsConstructor
public class CreateFormRequestDto {

    @NotNull
    private String json;

    @NotNull
    private FormType formType;
}
