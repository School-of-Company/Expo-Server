package team.startup.expo.domain.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import team.startup.expo.domain.admin.Authority;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ExpoParticipant {

    @Id
    @GeneratedValue(generator = "ulidGenerator")
    @GenericGenerator(name = "ulidGenerator", strategy = "team.startup.expo.global.common.ulid.ULIDGenerator")
    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private byte[] id;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(15)")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @Column(nullable = false)
    private boolean status;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String affiliation;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String position;

    @Column(nullable = false)
    private Boolean informationStatus;
}
