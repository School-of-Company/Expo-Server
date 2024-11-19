package team.startup.expo.domain.attendance.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotFoundStandardProgramUserException extends GlobalException {
    public NotFoundStandardProgramUserException() {
        super(ErrorCode.NOT_FOUND_STANDARD_PROGRAM_USER);
    }
}
