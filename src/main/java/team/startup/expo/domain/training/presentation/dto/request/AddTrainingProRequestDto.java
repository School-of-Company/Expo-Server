package team.startup.expo.domain.training.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import team.startup.expo.domain.training.Category;

@Getter
@NoArgsConstructor
public class AddTrainingProRequestDto {
    @NotNull
    private String title;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String startedAt;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String endedAt;

    @NotNull
    private Category category;
}
