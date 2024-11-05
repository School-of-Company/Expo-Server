package team.startup.expo.domain.training;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.trainee.Trainee;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TrainingProgramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean status = false;

    @Column(columnDefinition = "VARCHAR(20)")
    private String entryTime;

    @Column(columnDefinition = "VARCHAR(20)")
    private String leaveTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainingPro_id")
    private TrainingProgram trainingProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;
}
