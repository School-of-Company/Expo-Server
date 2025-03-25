package team.startup.expo.domain.participant.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class InvalidPageSizeException extends GlobalException {
    public InvalidPageSizeException() {
        super(ErrorCode.INVALID_PAGE_SIZE);
    }
}
