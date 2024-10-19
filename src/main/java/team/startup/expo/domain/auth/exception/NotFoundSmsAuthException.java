package team.startup.expo.domain.auth.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotFoundSmsAuthException extends GlobalException {
    public NotFoundSmsAuthException() {
        super(ErrorCode.NOT_FOUND_SMS_AUTH);
    }
}
