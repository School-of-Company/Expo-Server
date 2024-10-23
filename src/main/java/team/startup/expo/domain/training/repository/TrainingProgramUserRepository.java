package team.startup.expo.domain.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.trainee.Trainee;
import team.startup.expo.domain.training.TrainingProgramUser;

import java.util.List;

public interface TrainingProgramUserRepository extends JpaRepository<TrainingProgramUser, Long> {
    List<TrainingProgramUser> findByTrainee(Trainee traine);
}
