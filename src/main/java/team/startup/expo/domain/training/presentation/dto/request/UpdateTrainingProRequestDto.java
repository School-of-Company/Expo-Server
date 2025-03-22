package team.startup.expo.domain.training.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.training.entity.Category;
import team.startup.expo.domain.training.entity.TrainingProgram;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateTrainingProRequestDto {
    @NotNull
    private Long id;

    @NotNull
    private String title;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startedAt;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endedAt;

    @NotNull
    private Category category;

    public TrainingProgram toEntity(TrainingProgram trainingProgram) {
        return TrainingProgram.builder()
                .id(trainingProgram.getId())
                .title(title)
                .startedAt(String.valueOf(startedAt))
                .endedAt(String.valueOf(endedAt))
                .category(category)
                .expo(trainingProgram.getExpo())
                .build();
    }
}
