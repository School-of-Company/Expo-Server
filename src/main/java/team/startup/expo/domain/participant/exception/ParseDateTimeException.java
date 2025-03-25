package team.startup.expo.domain.participant.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class ParseDateTimeException extends GlobalException {
    public ParseDateTimeException() {
        super(ErrorCode.INVALID_DATE_FORMAT);
    }
}
