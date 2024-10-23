package team.startup.expo.domain.trainee.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetTrainingProgramResponseDto {
    private String programName;
}
