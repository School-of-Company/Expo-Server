package team.startup.expo.domain.standard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.standard.entity.StandardProgram;
import team.startup.expo.domain.standard.entity.StandardProgramUser;

import java.util.List;
import java.util.Optional;

public interface StandardProgramUserRepository extends JpaRepository<StandardProgramUser, Long> {
    Optional<StandardProgramUser> findByStandardProgramAndStandardParticipant(StandardProgram standardProgram, StandardParticipant standardParticipant);
    void deleteByStandardProgram(StandardProgram standardProgram);
    List<StandardProgramUser> findByStandardProgram(StandardProgram standardProgram);
    Boolean existsByStandardParticipantAndStandardProgram(StandardParticipant standardParticipant, StandardProgram standardProgram);
}
