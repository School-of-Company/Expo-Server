package team.startup.expo.domain.application.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.participant.entity.SchoolLevel;

@NoArgsConstructor
@Getter
public class ApplicationTemporaryQrRequestDto {
    @NotNull
    private String name;

    @NotNull
    private String affiliation;

    @NotNull
    private SchoolLevel schoolLevel;

    private String schoolDetail;

    @NotNull
    private String informationJson;

    @NotNull
    private Boolean personalInformationStatus;
}
