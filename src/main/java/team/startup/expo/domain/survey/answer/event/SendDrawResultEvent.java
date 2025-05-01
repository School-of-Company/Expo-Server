package team.startup.expo.domain.survey.answer.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SendDrawResultEvent {
    private String phoneNumber;
    private Integer drawNumber;
}
