package team.startup.expo.domain.user.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotFoundUserException extends GlobalException {
    public NotFoundUserException() {
        super(ErrorCode.NOT_FOUND_USER);
    }
}
