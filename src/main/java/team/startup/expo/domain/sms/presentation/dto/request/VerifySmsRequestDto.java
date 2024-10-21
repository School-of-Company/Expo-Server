package team.startup.expo.domain.sms.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifySmsRequestDto {
    private String phoneNumber;
    private String randomCode;
}
