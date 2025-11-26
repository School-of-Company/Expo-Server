package team.startup.expo.domain.training.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ApplicationTrainingProListAndTraineeRequestDto {

    @NotNull
    private String trainingId;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String name;

    @NotNull
    private String informationJson;

    @NotNull
    private Boolean personalInformationStatus;

    @NotEmpty
    private List<Long> trainingProIds;
}
