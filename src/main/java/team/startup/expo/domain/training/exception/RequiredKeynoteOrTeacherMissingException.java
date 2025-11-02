package team.startup.expo.domain.training.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class RequiredKeynoteOrTeacherMissingException extends GlobalException {
    public RequiredKeynoteOrTeacherMissingException() {
        super(ErrorCode.REQUIRED_KEYNOTE_OR_TEACHER_MISSING);
    }
}
