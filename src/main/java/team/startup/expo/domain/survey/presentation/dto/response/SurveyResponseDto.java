package team.startup.expo.domain.survey.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import team.startup.expo.domain.form.entity.FormType;
import team.startup.expo.domain.form.entity.ParticipationType;

import java.util.List;

@Getter
@Builder
public class SurveyResponseDto {
    private ParticipationType participationType;
    private List<DynamicSurveyResponseDto> dynamicSurveyResponseDto;

    @Getter
    @Builder
    public static class DynamicSurveyResponseDto {
        private String title;
        private String jsonData;
        private FormType formType;
        private Boolean requiredStatus;
        private String otherJson;
    }
}
