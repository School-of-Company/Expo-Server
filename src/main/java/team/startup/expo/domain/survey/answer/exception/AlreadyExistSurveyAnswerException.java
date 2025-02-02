package team.startup.expo.domain.survey.answer.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class AlreadyExistSurveyAnswerException extends GlobalException {
    public AlreadyExistSurveyAnswerException() {
        super(ErrorCode.ALREADY_EXIST_SURVEY_ANSWER);
    }
}
