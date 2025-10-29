package team.startup.expo.domain.form.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.form.entity.RegistrationType;

import java.util.List;
import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findByExpo(Expo expo);
    Boolean existsByExpoAndParticipationTypeAndRegistrationType(Expo expo, ParticipationType participationType, RegistrationType registrationType);
    Optional<Form> findByExpoAndParticipationTypeAndRegistrationType(Expo expo, ParticipationType participationType, RegistrationType registrationType);
    Optional<Form> findByExpoAndParticipationType(Expo expo, ParticipationType participationType);
    void deleteByExpo(Expo expo);
    List<Form> findAllByExpoIn(List<Expo> expos);
}
