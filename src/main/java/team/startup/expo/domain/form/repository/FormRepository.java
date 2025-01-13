package team.startup.expo.domain.form.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.entity.ParticipationType;

import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Long> {
    Optional<Form> findByExpo(Expo expo);
    Boolean existsByExpoAndParticipationType(Expo expo, ParticipationType participationType);
}
