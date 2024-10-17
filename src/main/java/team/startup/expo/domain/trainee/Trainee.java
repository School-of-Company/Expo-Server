package team.startup.expo.domain.trainee;

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
public class Trainee {

    @Id
    @GeneratedValue(generator = "ulidGenerator")
    @GenericGenerator(name = "ulidGenerator", strategy = "team.startup.expo.global.common.ulid.ULIDGenerator")
    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private byte[] id;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String trainingId;

    @Column(nullable = false)
    private Boolean laptopStatus;

    @Column(nullable = false, columnDefinition = "VARCHAR(15)")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Authority authority;
}
