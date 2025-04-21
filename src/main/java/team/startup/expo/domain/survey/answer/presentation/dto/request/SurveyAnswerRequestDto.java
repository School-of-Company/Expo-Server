package team.startup.expo.domain.survey.answer.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SurveyAnswerRequestDto {
    @NotNull
    private String phoneNumber;
    @NotNull
    private String answerJson;
    @NotNull
    private Boolean personalInformationStatus;
}
