package team.startup.expo.domain.training.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetTrainingProTraineeResponseDto {
    private String name;
    private String organization;
    private String position;
    private String programName;
    private Boolean status;
    private String entryTime;
    private String leaveTime;
}
