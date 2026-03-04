package team.startup.expo.domain.trainee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
import team.startup.expo.domain.expo.entity.Expo;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "tb_trainee")
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(15)")
    private String phoneNumber;

    @Column(nullable = false, columnDefinition = "VARCHAR(15)")
    private String trainingId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String informationJson;

    @Column(nullable = false)
    private Boolean personalInformationStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationType applicationType;

    @Column(nullable = false)
    private LocalDateTime applicationDate;

    @ManyToOne
    @JoinColumn(name = "expo_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Expo expo;
}
