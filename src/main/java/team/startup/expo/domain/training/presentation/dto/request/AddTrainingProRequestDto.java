package team.startup.expo.domain.training.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import team.startup.expo.domain.training.Category;

@Getter
@NoArgsConstructor
public class AddTrainingProRequestDto {
    private String title;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String startedAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String endedAt;
    private Category category;
}
