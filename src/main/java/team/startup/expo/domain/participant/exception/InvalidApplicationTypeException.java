package team.startup.expo.domain.participant.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class InvalidApplicationTypeException extends GlobalException {
    public InvalidApplicationTypeException() {
        super(ErrorCode.INVALID_APPLICATION_TYPE);
    }
}
