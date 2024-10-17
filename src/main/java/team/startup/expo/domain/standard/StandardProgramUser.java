package team.startup.expo.domain.standard;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import team.startup.expo.domain.participant.ExpoParticipant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class StandardProgramUser {

    @Id
    @GeneratedValue(generator = "ulidGenerator")
    @GenericGenerator(name = "ulidGenerator", strategy = "team.startup.expo.global.common.ulid.ULIDGenerator")
    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private byte[] id;

    @Column(nullable = false)
    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "standardPro_id")
    private StandardProgram standardProgram;

    @ManyToOne
    @JoinColumn(name = "expoPart_id")
    private ExpoParticipant expoParticipant;
}
