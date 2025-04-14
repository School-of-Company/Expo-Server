package team.startup.expo.domain.application.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.participant.entity.SchoolLevel;

@Getter
@NoArgsConstructor
public class ApplicationForParticipantRequestDto {

    @NotNull
    private String name;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String informationJson;

    @NotNull
    private Boolean personalInformationStatus;
}
