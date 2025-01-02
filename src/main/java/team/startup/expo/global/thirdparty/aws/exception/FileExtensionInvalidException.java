package team.startup.expo.global.thirdparty.aws.exception;

import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

public class FileExtensionInvalidException extends GlobalException {
    public FileExtensionInvalidException() {
        super(ErrorCode.FILE_EXTENSION_INVALID);
    }
}
