package team.startup.expo.domain.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.training.entity.TrainingProgram;
import team.startup.expo.domain.training.entity.TrainingProgramUser;

import java.util.List;
import java.util.Optional;

public interface TrainingProgramUserRepository extends JpaRepository<TrainingProgramUser, Long> {
    List<TrainingProgramUser> findByTrainee(Trainee traine);
    List<TrainingProgramUser> findByTrainingProgram(TrainingProgram trainingProgram);
    Optional<TrainingProgramUser> findByTraineeAndTrainingProgram(Trainee traine, TrainingProgram trainingProgram);
    Boolean existsByTraineeAndTrainingProgram(Trainee trainee, TrainingProgram trainingProgram);
    void deleteByTrainingProgram(TrainingProgram trainingProgram);
}
