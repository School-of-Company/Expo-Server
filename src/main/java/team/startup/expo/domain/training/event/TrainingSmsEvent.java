package team.startup.expo.domain.training.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team.startup.expo.domain.training.entity.TrainingProgram;

import java.util.List;

@Getter
@AllArgsConstructor
public class TrainingSmsEvent {
    private String expoId;
    private String phoneNumber;
    private List<TrainingProgram> trainingPrograms;
}
