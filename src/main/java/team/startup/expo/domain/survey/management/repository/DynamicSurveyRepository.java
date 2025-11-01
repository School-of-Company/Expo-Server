package team.startup.expo.domain.survey.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.survey.management.entity.DynamicSurvey;
import team.startup.expo.domain.survey.management.repository.custom.DynamicSurveyCustomRepository;

import java.util.List;

public interface DynamicSurveyRepository extends JpaRepository<DynamicSurvey, Long>, DynamicSurveyCustomRepository {
    List<DynamicSurvey> findBySurveyId(Long surveyId);
}
