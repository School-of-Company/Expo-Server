package team.startup.expo.domain.survey.management.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.form.entity.FormType;
import team.startup.expo.domain.form.entity.ParticipationType;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SurveyResponseDto {
    private String informationText;
    private ParticipationType participationType;
    private List<DynamicSurveyResponseDto> dynamicSurveyResponseDto;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicSurveyResponseDto {
        private String title;
        private String jsonData;
        private FormType formType;
        private Boolean requiredStatus;
        private String otherJson;
    }
}
