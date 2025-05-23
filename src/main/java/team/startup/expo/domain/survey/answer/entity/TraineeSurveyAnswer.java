package team.startup.expo.domain.survey.answer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import team.startup.expo.domain.trainee.entity.Trainee;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "tb_trainee_survey_answer")
public class TraineeSurveyAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answerJson;

    @ManyToOne
    @JoinColumn(name = "trainee_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Trainee trainee;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean personalInformationStatus;
}
