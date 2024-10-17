package team.startup.expo.domain.standard;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import team.startup.expo.domain.admin.Admin;
import team.startup.expo.domain.expo.Expo;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class StandardProgram {

    @Id
    @GeneratedValue(generator = "ulidGenerator")
    @GenericGenerator(name = "ulidGenerator", strategy = "team.startup.expo.global.common.ulid.ULIDGenerator")
    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private byte[] id;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String title;

    @ManyToOne
    @JoinColumn(name = "expo_id")
    private Expo expo;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;
}
