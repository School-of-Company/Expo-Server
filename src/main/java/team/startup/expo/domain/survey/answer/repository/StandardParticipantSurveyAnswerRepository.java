package team.startup.expo.domain.survey.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.survey.answer.entity.StandardParticipantSurveyAnswer;

public interface StandardParticipantSurveyAnswerRepository extends JpaRepository<StandardParticipantSurveyAnswer, Long> {
    Boolean existsByStandardParticipant(StandardParticipant standardParticipant);
}
