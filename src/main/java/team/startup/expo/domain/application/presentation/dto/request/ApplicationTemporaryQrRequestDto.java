package team.startup.expo.domain.application.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ApplicationTemporaryQrRequestDto {
    @NotNull
    private String name;

    @NotNull
    private String informationJson;

    @NotNull
    private Boolean personalInformationStatus;
}
