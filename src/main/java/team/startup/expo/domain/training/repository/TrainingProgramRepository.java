package team.startup.expo.domain.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.training.TrainingProgram;

public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, Long> {
}
