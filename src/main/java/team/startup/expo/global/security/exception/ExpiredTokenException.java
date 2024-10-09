package team.startup.expo.global.security.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class ExpiredTokenException extends GlobalException {
    public ExpiredTokenException() {
        super(ErrorCode.EXPIRED_TOKEN);
    }
}
