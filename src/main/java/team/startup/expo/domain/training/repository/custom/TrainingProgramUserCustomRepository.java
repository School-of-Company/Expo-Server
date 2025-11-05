package team.startup.expo.domain.training.repository.custom;

import java.util.Collection;
import java.util.Map;

public interface TrainingProgramUserCustomRepository {
    Map<Long, Integer> countTrainingProgramUserByTrainingProIdIn(Collection<Long> trainingProIds);
}
