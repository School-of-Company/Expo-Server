package team.startup.expo.domain.participant.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class InvalidExpoIdException extends GlobalException {
    public InvalidExpoIdException() {
        super(ErrorCode.INVALID_EXPO_ID);
    }
}
