package team.startup.expo.domain.trainee.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetTraineeInformationResponseDto {
    private String name;
    private List<GetTrainingProgramResponseDto> trainingProgram;
    private String trainingId;
    private Boolean laptopStatus;
}
