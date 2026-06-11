package team.startup.expo.domain.training.repository.custom;

import team.startup.expo.domain.training.entity.TrainingProgramUser;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TrainingProgramUserCustomRepository {
    Map<Long, Integer> countTrainingProgramUserByTrainingProIdIn(Collection<Long> trainingProIds);
    List<TrainingProgramUser> findByTrainingProgramIdWithFetch(Long trainingProId);
}
