package team.startup.expo.domain.training.entity;

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
@Table(name = "tb_training_program")
public class TrainingProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String title;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String startedAt;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String endedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "expo_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Expo expo;
}
