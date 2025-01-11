package team.startup.expo.domain.standard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.participant.entity.StandardParticipant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
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

    @ManyToOne
    @JoinColumn(name = "standardPro_id")
    private StandardProgram standardProgram;

    @ManyToOne
    @JoinColumn(name = "standard_participant_id")
    private StandardParticipant standardParticipant;

    public void addLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }
}
