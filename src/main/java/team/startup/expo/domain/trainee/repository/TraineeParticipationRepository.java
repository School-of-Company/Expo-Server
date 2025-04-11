package team.startup.expo.domain.trainee.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.entity.TraineeParticipation;

import java.time.LocalDate;
import java.util.Optional;

public interface TraineeParticipationRepository extends JpaRepository<TraineeParticipation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT tp " +
            "FROM TraineeParticipation tp " +
            "WHERE tp.expo=:expo " +
            "AND tp.trainee=:trainee AND tp.attendanceDate=:attendanceDate")
    Optional<TraineeParticipation> findByExpoAndTraineeAndAttendanceDateForWrite(Expo expo, Trainee trainee, LocalDate attendanceDate);
}
