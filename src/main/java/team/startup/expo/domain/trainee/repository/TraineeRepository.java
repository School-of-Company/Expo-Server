package team.startup.expo.domain.trainee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.trainee.Trainee;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
}
