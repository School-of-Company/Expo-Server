package team.startup.expo.domain.participant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.participant.ExpoParticipant;
import team.startup.expo.domain.trainee.ParticipationType;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<ExpoParticipant, Long> {
    Optional<ExpoParticipant> findByPhoneNumberAndExpo(String phoneNumber, Expo expo);
    void deleteByExpo(Expo expo);
    List<ExpoParticipant> findByExpoAndParticipationType(Expo expo, ParticipationType participationType);
    Boolean existsByPhoneNumberAndExpo(String phoneNumber, Expo expo);
    List<ExpoParticipant> findByExpo(Expo expo);
}
