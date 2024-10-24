package team.startup.expo.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    // sms
    DUPLICATE_PHONE_NUMBER(409, "이미 존재하는 전화번호입니다."),
    NOT_FOUND_SMS_AUTH(404, "찾을 수 없는 sms입니다."),
    NOT_VERIFICATION_SMS(401, "인증되지 않은 sms입니다."),
    MANY_REQUEST_SMS_AUTH(429, "회원가입시 3번 초과로 sms 인증요청을 하였습니다."),
    NOT_MATCH_RANDOM_CODE(401, "랜덤 코드가 일치하지 않습니다."),

    // token
    EXPIRED_TOKEN(401, "토큰이 만료되었습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),

    // user
    NOT_FOUND_USER(404, "해당 유저를 찾을 수 없습니다."),
    DUPLICATE_NICKNAME(409, "이미 존재하는 닉네임입니다."),
    DUPLICATE_EMAIL(409, "이미 존재하는 이메일입니다."),

    // auth
    NOT_MATCH_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    ADMIN_STATE_PENDING(403, "아직 관리자가 보류 중입니다"),

    // training
    NOT_FOUND_TRAINING_PROGRAM(404, "연수 프로그램을 찾지 못했습니다."),

    // server
    INTERNAL_SERVER_ERROR(500, "예기치 못한 서버 에러");

    private final int status;
    private final String message;
}

