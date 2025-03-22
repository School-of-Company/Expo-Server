package team.startup.expo.domain.training.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;
import team.startup.expo.domain.training.entity.Category;

@Getter
@Builder
public class GetTrainingProResponse {
    private Long id;
    private String title;
    private String startedAt;
    private String endedAt;
    private Category category;
}
