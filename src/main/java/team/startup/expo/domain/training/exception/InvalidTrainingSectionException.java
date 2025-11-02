package team.startup.expo.domain.training.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class InvalidTrainingSectionException extends GlobalException {
    public InvalidTrainingSectionException() {
        super(ErrorCode.INVALID_TRAINING_SELECTION);
    }
}
