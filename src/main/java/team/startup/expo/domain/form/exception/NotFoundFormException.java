package team.startup.expo.domain.form.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotFoundFormException extends GlobalException {
    public NotFoundFormException() {
        super(ErrorCode.NOT_FOUND_FORM);
    }
}
