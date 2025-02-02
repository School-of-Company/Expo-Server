package team.startup.expo.domain.survey.answer.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StandardSurveyAnswerRequestDto {
    private String phoneNumber;
    private String answerJson;
}
