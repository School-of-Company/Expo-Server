package team.startup.expo.domain.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.admin.Authority;
import team.startup.expo.domain.expo.Expo;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ExpoParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(15)")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean status;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String affiliation;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String position;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean informationStatus;

    private byte[] qrCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expo_id")
    private Expo expo;

    public void addQrCode(byte[] qrCode) {
        this.qrCode = qrCode;
    }
}
