package team.startup.expo.domain.sms.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.admin.entity.Authority;

@Getter
@NoArgsConstructor
public class SendQrRequestDto {
    private String phoneNumber;
    private Authority authority;
}
