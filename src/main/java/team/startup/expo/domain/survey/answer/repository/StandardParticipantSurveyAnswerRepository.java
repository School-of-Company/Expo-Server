package team.startup.expo.domain.survey.answer.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.survey.answer.entity.StandardParticipantSurveyAnswer;

import java.util.List;

public interface StandardParticipantSurveyAnswerRepository extends JpaRepository<StandardParticipantSurveyAnswer, Long> {
    Boolean existsByStandardParticipant(StandardParticipant standardParticipant);

    @Query("SELECT spsa FROM StandardParticipantSurveyAnswer spsa WHERE spsa.standardParticipant.id IN :standardParticipantIds")
    List<StandardParticipantSurveyAnswer> findStandardParticipantSurveyAnswersByStandardParticipantIds(List<Long> standardParticipantIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT spsa FROM StandardParticipantSurveyAnswer spsa WHERE spsa.standardParticipant = :standardParticipant")
    StandardParticipantSurveyAnswer findByStandardParticipantForWrite(StandardParticipant standardParticipant);
}
