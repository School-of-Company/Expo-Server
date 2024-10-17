package team.startup.expo.domain.expo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import team.startup.expo.domain.admin.Admin;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Expo {

    @Id
    @GeneratedValue(generator = "ulidGenerator")
    @GenericGenerator(name = "ulidGenerator", strategy = "team.startup.expo.global.common.ulid.ULIDGenerator")
    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private byte[] id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String startedDay;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String finishedDay;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String location;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;
}
