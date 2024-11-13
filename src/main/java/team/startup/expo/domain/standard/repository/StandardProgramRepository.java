package team.startup.expo.domain.standard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.standard.StandardProgram;

public interface StandardProgramRepository extends JpaRepository<StandardProgram, Long> {
    void deleteByExpo(Expo expo);
}