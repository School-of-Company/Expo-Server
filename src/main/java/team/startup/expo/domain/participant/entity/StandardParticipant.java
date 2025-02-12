package team.startup.expo.domain.participant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.trainee.entity.ApplicationType;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "tb_standard_participant")
public class StandardParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(15)")
    private String phoneNumber;

    @Column(nullable = false)
    private String affiliation;

    @Column(nullable = false)
    private SchoolLevel schoolLevel;

    private String schoolDetail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String informationJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean personalInformationStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationType applicationType;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] qrCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expo_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Expo expo;

    public void addQrCode(byte[] qrCode) {
        this.qrCode = qrCode;
    }
}
