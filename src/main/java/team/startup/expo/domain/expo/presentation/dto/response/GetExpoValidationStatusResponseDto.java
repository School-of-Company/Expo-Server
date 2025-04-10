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
        private Boolean standardFormCreatedStatus;
        private Boolean traineeFormCreatedStatus;
        private Boolean StandardSurveyCreatedStatus;
        private Boolean traineeSurveyCreatedStatus;
    }

}