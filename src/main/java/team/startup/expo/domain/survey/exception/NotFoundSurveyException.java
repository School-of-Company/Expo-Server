package team.startup.expo.domain.survey.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotFoundSurveyException extends GlobalException {
    public NotFoundSurveyException() {
        super(ErrorCode.NOT_FOUND_SURVEY);
    }
}
