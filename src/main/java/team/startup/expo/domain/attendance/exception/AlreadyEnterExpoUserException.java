package team.startup.expo.domain.attendance.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class AlreadyEnterExpoUserException extends GlobalException {
    public AlreadyEnterExpoUserException() {
        super(ErrorCode.ALREADY_ENTER_EXPO_USER);
    }
}
