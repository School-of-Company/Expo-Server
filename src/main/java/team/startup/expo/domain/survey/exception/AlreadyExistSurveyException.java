package team.startup.expo.domain.survey.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class AlreadyExistSurveyException extends GlobalException {
    public AlreadyExistSurveyException() {
        super(ErrorCode.ALREADY_EXIST_SURVEY);
    }
}
