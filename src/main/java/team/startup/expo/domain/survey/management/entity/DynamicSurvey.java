package team.startup.expo.domain.survey.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.form.entity.FormType;
import team.startup.expo.domain.mongo.entity.DynamicSurveyJsonDoc;

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
    private Long surveyId;

    @Column(nullable = false)
    private String title;

    @Transient
    private String jsonData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormType formType;

    @Column(nullable = false)
    private Boolean requiredStatus;

    @Transient
    private String otherJson;

    @Transient
    private DynamicSurveyJsonDoc dynamicSurveyJsonDoc;
}
