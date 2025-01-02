package team.startup.expo.domain.expo.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotExistTraineeAtExpoException extends GlobalException {
    public NotExistTraineeAtExpoException() {
        super(ErrorCode.NOT_EXIST_TRAINEE_AT_EXPO);
    }
}
