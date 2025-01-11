package team.startup.expo.domain.participant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.trainee.entity.ApplicationType;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class StandardParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(15)")
    private String phoneNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String informationJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean personalInformationStatus;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean attendanceStatus = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationType applicationType;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] qrCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expo_id")
    private Expo expo;

    public void changeAttendanceStatus() {
        this.attendanceStatus = true;
    }

    public void addQrCode(byte[] qrCode) {
        this.qrCode = qrCode;
    }
}
