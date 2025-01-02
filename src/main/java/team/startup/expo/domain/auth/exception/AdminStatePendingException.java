package team.startup.expo.domain.auth.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class AdminStatePendingException extends GlobalException {
    public AdminStatePendingException() {
        super(ErrorCode.ADMIN_STATE_PENDING);
    }
}
