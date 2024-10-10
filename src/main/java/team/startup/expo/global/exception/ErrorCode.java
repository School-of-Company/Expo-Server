package team.startup.expo.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    // token
    EXPIRED_TOKEN(401, "토큰이 만료되었습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),

    // user
    NOT_FOUND_USER(404, "해당 유저를 찾을 수 없습니다."),

    // server
    INTERNAL_SERVER_ERROR(500, "예기치 못한 서버 에러");

    private final int status;
    private final String message;
}

