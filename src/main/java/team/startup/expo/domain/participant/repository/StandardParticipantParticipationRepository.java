package team.startup.expo.domain.participant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.entity.StandardParticipantParticipation;

import java.time.LocalDate;
import java.util.Optional;

public interface StandardParticipantParticipationRepository extends JpaRepository<StandardParticipantParticipation, Long> {
    Optional<StandardParticipantParticipation> findByExpoAndStandardParticipantAndAttendanceDate(Expo expo, StandardParticipant standardParticipant, LocalDate attendanceDate);
}
