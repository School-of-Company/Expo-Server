package team.startup.expo.domain.trainee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.trainee.Trainee;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByPhoneNumber(String phoneNumber);
    Optional<Trainee> findByTrainingId(String trainingId);
    void deleteByExpo(Expo expo);
}
