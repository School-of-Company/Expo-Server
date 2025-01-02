package team.startup.expo.domain.trainee.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;
import team.startup.expo.domain.trainee.ParticipationType;

import java.util.List;

@Getter
@Builder
public class GetTraineeInformationResponseDto {
    private Long id;
    private String name;
    private String trainingId;
    private Boolean laptopStatus;
    private String phoneNumber;
    private String position;
    private String schoolLevel;
    private String organization;
    private Boolean informationStatus;
    private ParticipationType participationType;
}
