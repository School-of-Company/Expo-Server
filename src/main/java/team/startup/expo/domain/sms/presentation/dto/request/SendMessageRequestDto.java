package team.startup.expo.domain.sms.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.admin.Authority;

@Getter
@NoArgsConstructor
public class SendMessageRequestDto {
    private String title;
    private String content;
    private Authority authority;
}
