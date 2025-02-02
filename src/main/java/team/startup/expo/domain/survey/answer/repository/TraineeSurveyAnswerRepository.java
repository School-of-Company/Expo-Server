package team.startup.expo.domain.survey.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.survey.answer.entity.TraineeSurveyAnswer;

public interface TraineeSurveyAnswerRepository extends JpaRepository<TraineeSurveyAnswer, Long> {
}
