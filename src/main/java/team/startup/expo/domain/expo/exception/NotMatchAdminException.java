package team.startup.expo.domain.expo.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotMatchAdminException extends GlobalException {
    public NotMatchAdminException() {
        super(ErrorCode.NOT_MATCH_ADMIN);
    }
}
