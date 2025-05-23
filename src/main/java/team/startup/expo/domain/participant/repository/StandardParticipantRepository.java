package team.startup.expo.domain.participant.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.trainee.entity.ApplicationType;

import java.util.List;
import java.util.Optional;

public interface StandardParticipantRepository extends JpaRepository<StandardParticipant, Long> {

    Optional<StandardParticipant> findByPhoneNumberAndExpo(String phoneNumber, Expo expo);

    @Query("SELECT sp FROM StandardParticipant sp " +
            "WHERE sp.phoneNumber = :phoneNumber " +
            "AND sp.expo=:expo")
    StandardParticipant findByPhoneNumberAndExpoForNullCheck(String phoneNumber, Expo expo);

    void deleteByExpo(Expo expo);

    Boolean existsByPhoneNumberAndExpo(String phoneNumber, Expo expo);

    List<StandardParticipant> findByExpo(Expo expo);

    Boolean existsByPhoneNumber(String phoneNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT sp FROM StandardParticipant sp WHERE sp.phoneNumber=:phoneNumber AND sp.expo=:expo")
    Optional<StandardParticipant> findByPhoneNumberAndExpoForWrite(String phoneNumber, Expo expo);
}
