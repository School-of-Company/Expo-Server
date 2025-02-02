package team.startup.expo.domain.survey.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.form.entity.ParticipationType;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "tb_survey")
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationType participationType;

    @ManyToOne
    @JoinColumn(name = "expo_id")
    private Expo expo;
}
