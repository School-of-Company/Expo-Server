package team.startup.expo.domain.survey.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.survey.management.entity.DynamicSurvey;

import java.util.List;

public interface DynamicSurveyRepository extends JpaRepository<DynamicSurvey, Long> {
    void deleteBySurveyId(Long surveyId);
    List<DynamicSurvey> findBySurveyId(Long surveyId);
}
