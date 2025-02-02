package team.startup.expo.domain.survey.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.survey.management.entity.Survey;

import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Boolean existsByExpoAndParticipationType(Expo expo, ParticipationType participationType);
    Optional<Survey> findByExpoAndParticipationType(Expo expo, ParticipationType participationType);
    List<Survey> findByExpo(Expo expo);
}
