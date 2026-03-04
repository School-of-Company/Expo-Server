package team.startup.expo.domain.form.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.trainee.entity.ApplicationType;

import java.util.List;
import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findByExpo(Expo expo);
    Boolean existsByExpoAndParticipationTypeAndApplicationType(Expo expo, ParticipationType participationType, ApplicationType applicationType);
    Optional<Form> findByExpoAndParticipationTypeAndApplicationType(Expo expo, ParticipationType participationType, ApplicationType applicationType);
    Optional<Form> findByExpoAndParticipationType(Expo expo, ParticipationType participationType);
    void deleteByExpo(Expo expo);
    List<Form> findAllByExpoIn(List<Expo> expos);
}
