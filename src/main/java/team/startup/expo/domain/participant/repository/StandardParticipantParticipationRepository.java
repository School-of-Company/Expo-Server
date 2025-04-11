package team.startup.expo.domain.participant.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.entity.StandardParticipantParticipation;

import java.time.LocalDate;
import java.util.Optional;

public interface StandardParticipantParticipationRepository extends JpaRepository<StandardParticipantParticipation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT spp FROM StandardParticipantParticipation spp " +
            "WHERE spp.expo=:expo " +
            "AND spp.standardParticipant=:standardParticipant " +
            "AND spp.attendanceDate=:attendanceDate")
    Optional<StandardParticipantParticipation> findByExpoAndStandardParticipantAndAttendanceDateForWrite(Expo expo, StandardParticipant standardParticipant, LocalDate attendanceDate);
}
