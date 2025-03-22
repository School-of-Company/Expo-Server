package team.startup.expo.domain.trainee.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;
import team.startup.expo.domain.trainee.entity.ApplicationType;

@Getter
@Builder
public class GetTraineeInformationResponseDto {
    private Long id;
    private String name;
    private String trainingId;
    private String phoneNumber;
    private ApplicationType applicationType;
}
