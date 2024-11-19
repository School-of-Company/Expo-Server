package team.startup.expo.domain.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.trainee.Trainee;
import team.startup.expo.domain.training.TrainingProgram;
import team.startup.expo.domain.training.TrainingProgramUser;

import java.util.List;
import java.util.Optional;

public interface TrainingProgramUserRepository extends JpaRepository<TrainingProgramUser, Long> {
    List<TrainingProgramUser> findByTrainee(Trainee traine);
    List<TrainingProgramUser> findByTrainingProgram(TrainingProgram trainingProgram);
    Optional<TrainingProgramUser> findByTraineeAndTrainingProgram(Trainee traine, TrainingProgram trainingProgram);
    Boolean existsByTraineeAndTrainingProgram(Trainee traine, TrainingProgram trainingProgram);
    void deleteByTrainingProgram(TrainingProgram trainingProgram);
}
