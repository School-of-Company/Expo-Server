package team.startup.expo.domain.form.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class OutOfRegistrationPeriodException extends GlobalException {
    public OutOfRegistrationPeriodException() {
        super(ErrorCode.OUT_OF_REGISTRATION_PERIOD);
    }
}
