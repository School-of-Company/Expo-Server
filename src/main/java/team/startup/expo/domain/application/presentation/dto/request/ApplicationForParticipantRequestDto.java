package team.startup.expo.domain.application.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplicationForParticipantRequestDto {

    @NotNull
    private String name;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String affiliation;

    @NotNull
    private String position;

    @NotNull
    private Boolean informationStatus;
}
