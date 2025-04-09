package team.startup.expo.domain.standard.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.standard.entity.StandardProgram;
import team.startup.expo.domain.standard.entity.StandardProgramUser;

import java.util.List;
import java.util.Optional;

public interface StandardProgramUserRepository extends JpaRepository<StandardProgramUser, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT spu FROM StandardProgramUser spu WHERE spu.standardProgram=:standardProgram AND spu.standardParticipant=:standardParticipant")
    Optional<StandardProgramUser> findByStandardProgramAndStandardParticipantForWrite(StandardProgram standardProgram, StandardParticipant standardParticipant);

    void deleteByStandardProgram(StandardProgram standardProgram);

    List<StandardProgramUser> findByStandardProgram(StandardProgram standardProgram);

    Boolean existsByStandardParticipantAndStandardProgram(StandardParticipant standardParticipant, StandardProgram standardProgram);
}
