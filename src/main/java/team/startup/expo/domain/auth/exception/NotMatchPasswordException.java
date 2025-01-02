package team.startup.expo.domain.auth.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotMatchPasswordException extends GlobalException {
    public NotMatchPasswordException() {
        super(ErrorCode.NOT_MATCH_PASSWORD);
    }
}
