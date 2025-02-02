package team.startup.expo.domain.survey.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.survey.answer.entity.TraineeSurveyAnswer;
import team.startup.expo.domain.trainee.entity.Trainee;

public interface TraineeSurveyAnswerRepository extends JpaRepository<TraineeSurveyAnswer, Long> {
    Boolean existsByTrainee(Trainee trainee);
}
