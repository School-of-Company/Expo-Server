package team.startup.expo.domain.survey.answer.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SurveyAnswerRequestDto {
    private String phoneNumber;
    private String answerJson;
}
