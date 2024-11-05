package team.startup.expo.domain.expo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.admin.Admin;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Expo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(columnDefinition = "TEXT")
    private String coverImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;
}
