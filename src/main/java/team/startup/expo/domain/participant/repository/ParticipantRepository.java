package team.startup.expo.domain.participant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.participant.ExpoParticipant;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<ExpoParticipant, Long> {
    Optional<ExpoParticipant> findByPhoneNumber(String phoneNumber);
    void deleteByExpo(Expo expo);
}
