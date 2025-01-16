package team.startup.expo.domain.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.form.presentation.dto.request.FormRequestDto;

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

    @Column(columnDefinition = "TEXT")
    private String informationImage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParticipationType participationType;

    @ManyToOne
    @JoinColumn(name = "expo_id", nullable = false)
    private Expo expo;

    public void updateForm(FormRequestDto dto) {
        this.informationImage = dto.getInformationImage();
        this.participationType = dto.getParticipantType();
    }
}
