package team.startup.expo.domain.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.survey.entity.DynamicSurvey;
import team.startup.expo.domain.survey.entity.Survey;

import java.util.List;

public interface DynamicSurveyRepository extends JpaRepository<DynamicSurvey, Long> {
    List<DynamicSurvey> findBySurvey(Survey survey);
    void deleteBySurvey(Survey survey);
}
