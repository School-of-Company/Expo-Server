package team.startup.expo.domain.participant.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class InvalidDateRangeException extends GlobalException {
    public InvalidDateRangeException() {
        super(ErrorCode.INVALID_DATE_RANGE);
    }
}
