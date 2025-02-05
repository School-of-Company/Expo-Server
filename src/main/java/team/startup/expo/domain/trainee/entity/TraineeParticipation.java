package team.startup.expo.domain.trainee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import team.startup.expo.domain.expo.entity.Expo;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "tb_trainee_participation")
public class TraineeParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    private LocalDateTime leaveTime;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    @JoinColumn(name = "trainee_id")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Trainee trainee;

    @JoinColumn(name = "expo_id")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Expo expo;

    public void addLeaveTime() {
        this.leaveTime = LocalDateTime.now();
    }
}
