package team.startup.expo.global.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> handler(GlobalException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.builder()
                .status(errorCode.getStatus())
                .message(errorCode.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(errorCode.getStatus()));
    }

}
