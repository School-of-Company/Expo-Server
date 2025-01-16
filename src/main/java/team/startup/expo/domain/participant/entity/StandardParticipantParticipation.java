package team.startup.expo.domain.participant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "tb_standard_participant_participation")
public class StandardParticipantParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    @Column(nullable = false)
    private LocalDateTime leaveTime;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    @JoinColumn(name = "standard_participant_id")
    @ManyToOne
    private StandardParticipant standardParticipant;
}
