package team.startup.expo.domain.survey.answer.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StandardSurveyAnswerResponseDto {
    private Boolean drawStatus;
    private Integer drawNumber;
    private String drawReason;
}
