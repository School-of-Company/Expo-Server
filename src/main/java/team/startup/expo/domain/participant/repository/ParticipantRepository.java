package team.startup.expo.domain.participant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.participant.StandardParticipant;
import team.startup.expo.domain.trainee.ApplicationType;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<StandardParticipant, Long> {
    Optional<StandardParticipant> findByPhoneNumberAndExpo(String phoneNumber, Expo expo);
    void deleteByExpo(Expo expo);
    List<StandardParticipant> findByExpoAndApplicationType(Expo expo, ApplicationType applicationType);
    Boolean existsByPhoneNumberAndExpo(String phoneNumber, Expo expo);
    List<StandardParticipant> findByExpo(Expo expo);
}
