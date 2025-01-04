package team.startup.expo.domain.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.expo.Expo;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class DynamicForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String form;

    private FormType formType;

    @ManyToOne
    @JoinColumn(name = "expo_id", nullable = false)
    private Expo expo;
}
