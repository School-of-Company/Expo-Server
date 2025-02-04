package team.startup.expo.domain.survey.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import team.startup.expo.domain.survey.management.entity.DynamicSurvey;
import team.startup.expo.domain.survey.management.entity.Survey;

import java.util.List;

public interface DynamicSurveyRepository extends JpaRepository<DynamicSurvey, Long> {
    List<DynamicSurvey> findBySurvey(Survey survey);
    @Modifying(clearAutomatically = true)
    @Query("DELETE DynamicSurvey ds WHERE ds.survey IN :surveys")
    void deleteBySurveys(List<Survey> surveys);
    void deleteBySurvey(Survey survey);
}
