package team.startup.expo.domain.standard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.participant.StandardParticipant;
import team.startup.expo.domain.standard.StandardProgram;
import team.startup.expo.domain.standard.StandardProgramUser;

import java.util.List;
import java.util.Optional;

public interface StandardProgramUserRepository extends JpaRepository<StandardProgramUser, Long> {
    Optional<StandardProgramUser> findByStandardProgramAndStandardParticipant(StandardProgram standardProgram, StandardParticipant standardParticipant);
    void deleteByStandardProgram(StandardProgram standardProgram);
    List<StandardProgramUser> findByStandardProgram(StandardProgram standardProgram);
}
