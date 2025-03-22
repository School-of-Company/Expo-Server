package team.startup.expo.domain.expo.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotInProgressExpoException extends GlobalException {
    public NotInProgressExpoException() {
        super(ErrorCode.NOT_IN_PROGRESS_EXPO);
    }
}
