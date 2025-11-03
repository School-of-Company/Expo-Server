package team.startup.expo.domain.training.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ApplicationTrainingProListAndTraineeRequestDto {

    @NotNull
    private String informationJson;

    @NotNull
    private Boolean personalInformationStatus;

    @NotNull
    private List<Long> trainingProIds;
}
