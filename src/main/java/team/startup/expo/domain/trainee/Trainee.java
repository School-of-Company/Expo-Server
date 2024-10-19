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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String trainingId;

    @Column(nullable = false)
    private Boolean laptopStatus;

    @Column(nullable = false, columnDefinition = "VARCHAR(15)")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private String position;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private String schoolLevel;

    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    private String organization;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private String name;

    @Column(nullable = false)
    private Boolean informationStatus;
}
