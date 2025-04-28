package team.startup.expo.domain.standard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.standard.entity.StandardProgram;

import java.util.List;
import java.util.Optional;

public interface StandardProgramRepository extends JpaRepository<StandardProgram, Long> {
    void deleteByExpo(Expo expo);
    List<StandardProgram> findByExpo(Expo expo);
    Optional<StandardProgram> findByIdAndExpoId(Long id, String expoId);
}
