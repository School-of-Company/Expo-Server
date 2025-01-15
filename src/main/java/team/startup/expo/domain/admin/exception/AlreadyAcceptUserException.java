package team.startup.expo.domain.admin.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class AlreadyAcceptUserException extends GlobalException {
    public AlreadyAcceptUserException() {
        super(ErrorCode.ALREADY_ACCEPT_USER);
    }
}
