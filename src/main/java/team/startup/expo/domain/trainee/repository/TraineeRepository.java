package team.startup.expo.domain.trainee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.trainee.entity.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByPhoneNumberAndExpo(String phoneNumber, Expo expo);
    List<Trainee> findByExpo(Expo expo);
    List<Trainee> findByExpoAndName(Expo expo, String name);
    void deleteByExpo(Expo expo);
    Boolean existsByPhoneNumberAndExpo(String phoneNumber, Expo expo);
    Optional<Trainee> findByTrainingId(String trainingId);
}
