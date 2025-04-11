package team.startup.expo.domain.attendance.exception;

import team.startup.expo.global.exception.GlobalException;

import static team.startup.expo.global.exception.ErrorCode.NOT_ENTER_AFTER_THIRTY_SECOND;

public class NotEnterAfterThirtySecondException extends GlobalException {
    public NotEnterAfterThirtySecondException() {
        super(NOT_ENTER_AFTER_THIRTY_SECOND);
    }
}
