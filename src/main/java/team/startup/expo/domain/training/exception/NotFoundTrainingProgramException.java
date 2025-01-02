package team.startup.expo.domain.training.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotFoundTrainingProgramException extends GlobalException {
    public NotFoundTrainingProgramException() {
        super(ErrorCode.NOT_FOUND_TRAINING_PROGRAM);
    }
}
