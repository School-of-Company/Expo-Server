package team.startup.expo.domain.expo.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotFoundExpoException extends GlobalException {
    public NotFoundExpoException() {
        super(ErrorCode.NOT_FOUND_EXPO);
    }
}
