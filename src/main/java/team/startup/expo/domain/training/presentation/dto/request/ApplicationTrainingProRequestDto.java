package team.startup.expo.domain.training.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ApplicationTrainingProRequestDto {
    @NotNull
    private Long trainingId;
}
