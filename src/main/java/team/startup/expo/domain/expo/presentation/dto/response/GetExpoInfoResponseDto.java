package team.startup.expo.domain.expo.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetExpoInfoResponseDto {
    private String title;
    private String description;
    private String startedDay;
    private String finishedDay;
    private String location;
    private String coverImage;
    private Float x;
    private Float y;
}
