package team.startup.expo.domain.sms.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotFoundParticipantException extends GlobalException {
    public NotFoundParticipantException() {
        super(ErrorCode.NOT_FOUND_PARTICIPANT);
    }
}
