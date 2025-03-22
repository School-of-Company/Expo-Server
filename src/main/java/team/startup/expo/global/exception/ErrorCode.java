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

    // file
    FILE_EXTENSION_INVALID(400, "파일 확장자가 유효하지 않습니다."),

    // token
    EXPIRED_TOKEN(401, "토큰이 만료되었습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),

    // user
    NOT_FOUND_USER(404, "해당 유저를 찾을 수 없습니다."),
    DUPLICATE_NICKNAME(409, "이미 존재하는 닉네임입니다."),
    DUPLICATE_EMAIL(409, "이미 존재하는 이메일입니다."),
    NOT_MATCH_ADMIN(403, "관리자가 일치하지 않습니다."),
    ALREADY_LEAVE_PROGRAM_USER(400, "이미 프로그램을 퇴실한 유저입니다."),
    ALREADY_APPLICATION_USER(409, "이미 신청한 유저입니다."),
    ALREADY_ACCEPT_USER(409, "이미 수락한 유저입니다."),

    // auth
    NOT_MATCH_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    ADMIN_STATE_PENDING(403, "아직 관리자가 보류 중입니다"),

    // training
    NOT_FOUND_TRAINING_PROGRAM(404, "연수 프로그램을 찾지 못했습니다."),
    NOT_FOUND_TRAINING_PROGRAM_USER(404, "연수 프로그램을 참가 중인 유저를 찾지 못 했습니다."),

    // standard
    NOT_FOUND_STANDARD_PROGRAM(404, "일반 프로그램을 찾지 못 했습니다."),
    NOT_FOUND_STANDARD_PROGRAM_USER(404, "일반 프로그램을 참가 중인 유저를 찾지 못 했습니다."),

    // application
    NOT_ENTER_SCHOOL_DETAIL(400, "상세한 학교 이름을 입력하지 않았습니다."),

    // expo
    NOT_FOUND_EXPO(404, "박람회를 찾지 못 했습니다."),
    NOT_EXIST_TRAINEE_AT_EXPO(404, "해당 박람회에 연수자가 존재하지 않습니다."),
    NOT_EXIST_PARTICIPANT_AT_EXPO(404, "해당 박람회에 관람객이 존재하지 않습니다."),
    NOT_IN_PROGRESS_EXPO(400, "해당 박람회는 진행 중인 상태가 아닙니다."),

    // participant
    NOT_FOUND_PARTICIPANT(404, "행사 참가자를 찾지 못 했습니다."),

    // attendance
    ALREADY_ENTER_EXPO_USER(400, "이미 박람회에 입장한 유저입니다."),

    // trainee
    NOT_FOUND_TRAINEE(404, "연수자를 찾지 못 했습니다."),

    // form
    ALREADY_EXIST_FORM(409, "이미 폼이 존재합니다."),
    NOT_FOUND_FORM(404, "폼을 찾을 수 없습니다."),

    // survey
    ALREADY_EXIST_SURVEY(409, "이미 설문조사 폼이 존재합니다."),
    NOT_FOUND_SURVEY(404, "설문조사 폼을 찾을 수 없습니다."),
    ALREADY_EXIST_SURVEY_ANSWER(409, "이미 설문조사를 참여하였습니다."),

    // server
    INTERNAL_SERVER_ERROR(500, "예기치 못한 서버 에러");

    private final int status;
    private final String message;
}

