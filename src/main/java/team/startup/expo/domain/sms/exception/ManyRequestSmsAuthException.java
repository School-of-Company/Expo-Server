package team.startup.expo.domain.sms.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class ManyRequestSmsAuthException extends GlobalException {
    public ManyRequestSmsAuthException() {
        super(ErrorCode.MANY_REQUEST_SMS_AUTH);
    }
}
