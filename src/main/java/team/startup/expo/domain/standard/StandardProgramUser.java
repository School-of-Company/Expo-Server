package team.startup.expo.domain.standard;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class StandardProgramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean status = false;

    @Column(columnDefinition = "VARCHAR(20)")
    private String entryTime;

    @Column(columnDefinition = "VARCHAR(20)")
    private String leaveTime;
}
