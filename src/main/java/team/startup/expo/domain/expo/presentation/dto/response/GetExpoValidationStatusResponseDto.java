package team.startup.expo.domain.expo.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetExpoValidationStatusResponseDto {
    private boolean dynamicFormCreatedStatus;
    private boolean surveyCreatedStatus;
}
