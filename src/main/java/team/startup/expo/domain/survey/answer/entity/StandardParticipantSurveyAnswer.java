package team.startup.expo.domain.survey.answer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
import team.startup.expo.domain.participant.entity.StandardParticipant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "tb_standard_participant_survey_answer")
public class StandardParticipantSurveyAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String answerJson;

    @ManyToOne
    @JoinColumn(name = "standard_participant_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StandardParticipant standardParticipant;

    @Column(nullable = false)
    private Boolean personalInformationStatus;
}
