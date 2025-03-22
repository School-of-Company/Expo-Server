package team.startup.expo.domain.trainee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.entity.TraineeParticipation;

import java.time.LocalDate;
import java.util.Optional;

public interface TraineeParticipationRepository extends JpaRepository<TraineeParticipation, Long> {
    Optional<TraineeParticipation> findByExpoAndTraineeAndAttendanceDate(Expo expo, Trainee trainee, LocalDate attendanceDate);
}
