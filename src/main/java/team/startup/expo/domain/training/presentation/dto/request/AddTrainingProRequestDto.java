package team.startup.expo.domain.training.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import team.startup.expo.domain.training.Category;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AddTrainingProRequestDto {

    @NotNull
    private String title;

    @NotNull
    @Size(max = 20)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startedAt;

    @NotNull
    @Size(max = 20)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endedAt;

    @NotNull
    private Category category;
}
