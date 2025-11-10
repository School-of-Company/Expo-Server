package team.startup.expo.domain.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.form.presentation.dto.request.FormRequestDto;
import team.startup.expo.domain.trainee.entity.ApplicationType;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "tb_form")
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String informationText;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParticipationType participationType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationType applicationType;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "expo_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Expo expo;

    public void updateForm(FormRequestDto dto) {
        this.title = dto.getTitle();
        this.informationText = dto.getInformationText();
        this.participationType = dto.getParticipantType();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
    }
}
