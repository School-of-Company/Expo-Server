package team.startup.expo.domain.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.training.TrainingProgram;

import java.util.List;

public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, Long> {
    void deleteByExpo(Expo expo);
    List<TrainingProgram> findByExpo(Expo expo);
}
