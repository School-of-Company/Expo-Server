package team.startup.expo.domain.standard.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStandardProgramResponseDto {
    private Long id;
    private String title;
    private String startedAt;
    private String endedAt;
}
