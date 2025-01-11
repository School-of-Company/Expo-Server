package team.startup.expo.domain.expo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.admin.entity.Admin;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Expo {

    @Id
    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

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

    @Column(nullable = false)
    private Float x;

    @Column(nullable = false)
    private Float y;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;
}
