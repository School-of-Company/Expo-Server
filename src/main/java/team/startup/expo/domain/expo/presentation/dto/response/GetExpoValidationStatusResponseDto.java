package team.startup.expo.domain.expo.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetExpoValidationStatusResponseDto {

    private List<ExpoValidDto> expoValid;

    @Getter
    @Builder
    public static class ExpoValidDto {
        private String expoId;
        private Boolean preStandardFormCreatedStatus;
        private Boolean siteStandardFormCreatedStatus;
        private Boolean traineeFormCreatedStatus;
        private Boolean StandardSurveyCreatedStatus;
        private Boolean traineeSurveyCreatedStatus;
    }

}