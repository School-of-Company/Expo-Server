package team.startup.expo.domain.trainee;

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
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String trainingId;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean laptopStatus;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(15)")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private String position;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private String schoolLevel;

    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    private String organization;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private String name;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean informationStatus;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean attendanceStatus = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationType participationType;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] qrCode;

    @ManyToOne
    @JoinColumn(name = "expo_id")
    private Expo expo;

    public void changeAttendanceStatus() {
        this.attendanceStatus = true;
    }

    public void addQrCode(byte[] qrCode) {
        this.qrCode = qrCode;
    }
}
