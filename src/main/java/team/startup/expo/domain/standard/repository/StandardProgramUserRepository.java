package team.startup.expo.domain.standard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.participant.ExpoParticipant;
import team.startup.expo.domain.standard.StandardProgram;
import team.startup.expo.domain.standard.StandardProgramUser;

import java.util.Optional;

public interface StandardProgramUserRepository extends JpaRepository<StandardProgramUser, Long> {
    Optional<StandardProgramUser> findByStandardProgramAndExpoParticipant(StandardProgram standardProgram, ExpoParticipant expoParticipant);
    Boolean existsByExpoParticipantAndStandardProgram(ExpoParticipant expoParticipant, StandardProgram standardProgram);
    void deleteByStandardProgram(StandardProgram standardProgram);
}
