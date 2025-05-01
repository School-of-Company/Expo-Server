package team.startup.expo.domain.survey.answer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Draw {

    THIRTY(30, "정보원 건물 번호"),
    SIXTY_TWO(62, "광주 지역번호"),
    SEVENTY_ONE(71, "프로그램 참여 학교 수"),
    ONE_HUNDRED_ELEVEN(111, "정보원 주소 189번길"),
    ONE_HUNDRED_EIGHTY_NINE(189, "정보원 주소 189번길"),
    TWO_HUNDRED_NINE(209, "학급 수"),
    FIVE_HUNDRED_TWENTY_FOUR(524, "축전 날짜"),
    SEVEN_HUNDRED_SEVENTY_SEVEN(777, "행운의 번호"),
    ONE_THOUSAND_ONE(1001, "0과 1로 이루어진 코드"),
    ONE_THOUSAND_TEN(1010, "0과 1로 이루어진 코드"),
    ONE_THOUSAND_ONE_HUNDRED(1100, "0과 1로 이루어진 코드"),
    ONE_THOUSAND_TWO_HUNDRED_THIRTY_FOUR(1234, "순차번호"),
    ONE_THOUSAND_EIGHT_HUNDRED_NINETY_THREE(1893, "정보원 길, 건물번호"),
    TWO_THOUSAND(2000, "밀레니엄"),
    TWO_THOUSAND_TWENTY_FIVE(2025, "올해 연도"),
    TWO_THOUSAND_TWO_HUNDRED_TWENTY_TWO(2222, "반복되는 숫자"),
    THREE_THOUSAND_THREE_HUNDRED_THIRTY_THREE(3333, "반복되는 숫자"),
    THREE_THOUSAND_EIGHT_HUNDRED(3800, "정보원 국번 380"),
    FOUR_THOUSAND_FOUR_HUNDRED_FORTY_FOUR(4444, "반복되는 숫자"),
    FOUR_THOUSAND_SEVEN_HUNDRED_SIXTY(4760, "정보원 전화번호"),
    FIVE_THOUSAND(5000, "중간"),
    FIVE_THOUSAND_FIVE_HUNDRED_FIFTY_FIVE(5555, "반복되는 숫자"),
    FIVE_THOUSAND_SIX_HUNDRED_SEVENTY_FOUR(5674, "정보원 전화번호 뒷자리"),
    SIX_THOUSAND_SIX_HUNDRED_SIXTY_SIX(6666, "반복되는 숫자"),
    SIX_THOUSAND_SIX_HUNDRED_SEVENTY_FOUR(6674, "정보원 전화번호 뒷자리"),
    SEVEN_THOUSAND_ONE_HUNDRED_SEVENTY(7170, "1학기 프로그램 종료일"),
    SEVEN_THOUSAND_SIX_HUNDRED_SEVENTY_FOUR(7674, "정보원 전화번호 뒷자리"),
    SEVEN_THOUSAND_SEVEN_HUNDRED_SEVENTY_SEVEN(7777, "코드 느낌"),
    EIGHT_THOUSAND_SIX_HUNDRED_SEVENTY_FOUR(8674, "정보원 전화번호 뒷자리"),
    EIGHT_THOUSAND_EIGHT_HUNDRED_EIGHTY_EIGHT(8888, "반복되는 숫자"),
    NINE_THOUSAND_SIX_HUNDRED_SEVENTY_FOUR(9674, "정보원 전화번호 뒷자리"),
    NINE_THOUSAND_NINE_HUNDRED_NINETY_NINE(9999, "반복되는 숫자"),
    TEN_THOUSAND(10000, "끝");

    private final Integer number;
    private final String reason;
}
