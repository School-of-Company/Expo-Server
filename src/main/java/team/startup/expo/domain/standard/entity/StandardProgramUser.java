package team.startup.expo.domain.standard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import team.startup.expo.domain.participant.entity.StandardParticipant;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "tb_standard_program_user")
public class StandardProgramUser {

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
    @JoinColumn(name = "standardPro_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StandardProgram standardProgram;

    @ManyToOne
    @JoinColumn(name = "standard_participant_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StandardParticipant standardParticipant;

    public void addLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }
}
