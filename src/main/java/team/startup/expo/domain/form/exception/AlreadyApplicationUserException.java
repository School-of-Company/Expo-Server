package team.startup.expo.domain.form.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class AlreadyApplicationUserException extends GlobalException {
    public AlreadyApplicationUserException() {
        super(ErrorCode.ALREADY_APPLICATION_USER);
    }
}
