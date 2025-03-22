package team.startup.expo.domain.training.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import team.startup.expo.domain.trainee.entity.Trainee;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "tb_training_program_user")
public class TrainingProgramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean status = false;

    @Column(columnDefinition = "VARCHAR(20)")
    private String entryTime;

    @Column(columnDefinition = "VARCHAR(20)")
    private String leaveTime;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    @ManyToOne
    @JoinColumn(name = "trainingPro_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TrainingProgram trainingProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Trainee trainee;
}
