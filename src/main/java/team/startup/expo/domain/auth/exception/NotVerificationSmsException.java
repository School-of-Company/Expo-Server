package team.startup.expo.domain.auth.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotVerificationSmsException extends GlobalException {
    public NotVerificationSmsException() {
        super(ErrorCode.NOT_VERIFICATION_SMS);
    }
}
