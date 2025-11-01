package team.startup.expo.domain.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.mongo.entity.DynamicFormJsonDoc;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "tb_dynamic_form")
public class DynamicForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long formId;

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
    private DynamicFormJsonDoc formJson;

    @Builder
    private DynamicForm(Long id, Long formId, String title, FormType formType, Boolean requiredStatus) {
        this.id = id;
        this.formId = formId;
        this.title = title;
        this.formType = formType;
        this.requiredStatus = requiredStatus;
    }
}
