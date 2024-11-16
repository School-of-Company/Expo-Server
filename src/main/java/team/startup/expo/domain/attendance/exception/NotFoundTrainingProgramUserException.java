package team.startup.expo.domain.attendance.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotFoundTrainingProgramUserException extends GlobalException {
    public NotFoundTrainingProgramUserException() {
        super(ErrorCode.NOT_FOUND_TRAINING_PROGRAM_USER);
    }
}
