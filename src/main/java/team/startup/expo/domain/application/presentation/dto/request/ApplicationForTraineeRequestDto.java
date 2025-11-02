package team.startup.expo.domain.application.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ApplicationForTraineeRequestDto {

    private String trainingId;

    private String name;

    private String phoneNumber;

    @NotNull
    private String informationJson;

    @NotNull
    private Boolean personalInformationStatus;
}
