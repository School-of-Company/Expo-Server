package team.startup.expo.domain.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import team.startup.expo.domain.expo.entity.Expo;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "tb_dynamic_form")
public class DynamicForm {

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

    @JoinColumn(name = "form_id")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Form form;

    @ManyToOne
    @JoinColumn(name = "expo_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Expo expo;
}
