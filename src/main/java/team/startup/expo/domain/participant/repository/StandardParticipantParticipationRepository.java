package team.startup.expo.domain.participant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.participant.entity.StandardParticipantParticipation;

public interface StandardParticipantParticipationRepository extends JpaRepository<StandardParticipantParticipation, Long> {
}
