package team.startup.expo.domain.trainee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.trainee.entity.TraineeParticipation;

public interface TraineeParticipationRepository extends JpaRepository<TraineeParticipation, Long> {
}
