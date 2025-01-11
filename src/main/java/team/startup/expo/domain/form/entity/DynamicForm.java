package team.startup.expo.domain.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DynamicForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Lob
    private String jsonData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormType formType;

    @JoinColumn(name = "expo_id")
    @ManyToOne
    private Expo expo;
}
