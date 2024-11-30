package team.startup.expo.domain.trainee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.trainee.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByPhoneNumber(String phoneNumber);
    List<Trainee> findByExpo(Expo expo);
    void deleteByExpo(Expo expo);
    Boolean existsByPhoneNumber(String phoneNumber);
    Optional<Trainee> findByTrainingId(String trainingId);
}
