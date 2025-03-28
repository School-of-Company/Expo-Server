package team.startup.expo.domain.participant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.trainee.entity.ApplicationType;

import java.util.List;
import java.util.Optional;

public interface StandardParticipantRepository extends JpaRepository<StandardParticipant, Long> {
    Optional<StandardParticipant> findByPhoneNumberAndExpo(String phoneNumber, Expo expo);
    void deleteByExpo(Expo expo);
    @Query("SELECT sp FROM StandardParticipant sp " +
            "JOIN sp.expo e " +
            "WHERE e = :expo " +
            "AND sp.applicationType = :applicationType " +
            "AND sp.name LIKE %:name%")
    List<StandardParticipant> findByExpoAndApplicationTypeAndName(Expo expo, ApplicationType applicationType, String name);
    List<StandardParticipant> findByExpoAndApplicationType(Expo expo, ApplicationType applicationType);
    Boolean existsByPhoneNumberAndExpo(String phoneNumber, Expo expo);
    List<StandardParticipant> findByExpo(Expo expo);
    Boolean existsByPhoneNumber(String phoneNumber);
}
