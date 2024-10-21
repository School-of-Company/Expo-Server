package team.startup.expo.domain.sms.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotMatchRandomCodeException extends GlobalException {
    public NotMatchRandomCodeException() {
        super(ErrorCode.NOT_MATCH_RANDOM_CODE);
    }
}
