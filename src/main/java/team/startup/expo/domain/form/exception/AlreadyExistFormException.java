package team.startup.expo.domain.form.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class AlreadyExistFormException extends GlobalException {
    public AlreadyExistFormException() {
        super(ErrorCode.ALREADY_EXIST_FORM);
    }
}
