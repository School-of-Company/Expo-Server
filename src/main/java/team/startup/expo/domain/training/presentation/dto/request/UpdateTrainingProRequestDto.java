package team.startup.expo.domain.training.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import team.startup.expo.domain.training.Category;
import team.startup.expo.domain.training.TrainingProgram;

@Getter
@NoArgsConstructor
public class UpdateTrainingProRequestDto {
    private String title;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String startedAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String endedAt;
    private Category category;

    public TrainingProgram toEntity(TrainingProgram trainingProgram) {
        return TrainingProgram.builder()
                .id(trainingProgram.getId())
                .title(title)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .category(category)
                .expo(trainingProgram.getExpo())
                .build();
    }
}
