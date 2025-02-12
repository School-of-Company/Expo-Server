package team.startup.expo.domain.application.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotEnterSchoolDetailException extends GlobalException {
    public NotEnterSchoolDetailException() {
        super(ErrorCode.NOT_ENTER_SCHOOL_DETAIL);
    }
}
