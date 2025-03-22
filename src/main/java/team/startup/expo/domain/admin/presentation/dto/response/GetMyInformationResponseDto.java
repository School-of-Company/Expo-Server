package team.startup.expo.domain.admin.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMyInformationResponseDto {
    private String name;
    private String nickname;
    private String email;
}
