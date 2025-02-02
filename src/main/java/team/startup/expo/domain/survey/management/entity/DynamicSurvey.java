package team.startup.expo.domain.survey.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.form.entity.FormType;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "tb_dynamic_survey")
public class DynamicSurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String jsonData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormType formType;

    @Column(nullable = false)
    private Boolean requiredStatus;

    @Column(columnDefinition = "TEXT")
    private String otherJson;

    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;
}
