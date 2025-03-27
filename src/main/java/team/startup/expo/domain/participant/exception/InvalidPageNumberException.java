package team.startup.expo.domain.participant.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class InvalidPageNumberException extends GlobalException {
    public InvalidPageNumberException() {
        super(ErrorCode.INVALID_PAGE_NUMBER);
    }
}
