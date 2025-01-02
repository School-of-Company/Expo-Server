package team.startup.expo.domain.expo.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetExpoResponseDto {
    private String id;
    private String title;
    private String description;
    private String startedDay;
    private String finishedDay;
    private String coverImage;
}
