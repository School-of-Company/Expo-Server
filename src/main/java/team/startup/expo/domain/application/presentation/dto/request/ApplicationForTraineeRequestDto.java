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
    private String name;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String informationJson;

    @NotNull
    private Boolean personalInformationStatus;
}
