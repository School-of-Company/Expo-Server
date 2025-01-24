package team.startup.expo.domain.sms.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team.startup.expo.domain.admin.entity.Authority;

@Getter
@AllArgsConstructor
public class SendQrEvent {
    private String expoId;
    private String phoneNumber;
    private Authority authority;
}
