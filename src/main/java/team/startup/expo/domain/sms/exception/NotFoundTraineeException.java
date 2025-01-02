package team.startup.expo.domain.sms.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotFoundTraineeException extends GlobalException {
    public NotFoundTraineeException() {
        super(ErrorCode.NOT_FOUND_TRAINEE);
    }
}
