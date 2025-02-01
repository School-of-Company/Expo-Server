package team.startup.expo.domain.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.survey.entity.DynamicSurvey;

public interface DynamicSurveyRepository extends JpaRepository<DynamicSurvey, Long> {
}
