package team.startup.expo.domain.attendance.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class AlreadyLeaveProgramUserException extends GlobalException {
    public AlreadyLeaveProgramUserException() {
        super(ErrorCode.ALREADY_LEAVE_PROGRAM_USER);
    }
}
