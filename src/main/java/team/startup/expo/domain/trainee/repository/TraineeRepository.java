package team.startup.expo.domain.trainee.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.trainee.entity.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByPhoneNumberAndExpo(String phoneNumber, Expo expo);

    List<Trainee> findByExpo(Expo expo);

    @Query("SELECT t FROM Trainee t " +
            "JOIN t.expo e " +
            "WHERE e = :expo " +
            "AND t.name LIKE %:name%")
    List<Trainee> findByExpoAndName(Expo expo, String name);

    void deleteByExpo(Expo expo);

    Boolean existsByPhoneNumberAndExpo(String phoneNumber, Expo expo);

    Optional<Trainee> findByTrainingId(String trainingId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Trainee t WHERE t.phoneNumber=:phoneNumber AND t.expo=:expo")
    Optional<Trainee> findByPhoneNumberAndExpoForWrite(String phoneNumber, Expo expo);
}
