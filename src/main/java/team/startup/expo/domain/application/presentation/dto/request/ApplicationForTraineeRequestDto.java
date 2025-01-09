package team.startup.expo.domain.application.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ApplicationForTraineeRequestDto {

    @NotNull
    private String trainingId;

    @NotNull
    private Boolean laptopStatus;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String position;

    @NotNull
    private String schoolLevel;

    @NotNull
    private String organization;

    @NotNull
    private String name;

    @NotNull
    private Boolean informationStatus;
}
