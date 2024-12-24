package team.startup.expo.domain.expo.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class NotExistParticipantAtExpoException extends GlobalException {
    public NotExistParticipantAtExpoException() {
        super(ErrorCode.NOT_EXIST_PARTICIPANT_AT_EXPO);
    }
}
