package team.startup.expo.domain.participant.repository.custom;

import org.springframework.data.domain.Pageable;
import team.startup.expo.domain.participant.presentation.dto.response.ParticipantResponseDto;
import team.startup.expo.domain.trainee.entity.ApplicationType;

import java.time.LocalDate;

public interface ParticipantCustomRepository {
    ParticipantResponseDto searchParticipants(String expoId, ApplicationType type, String name, Pageable pageable, LocalDate date);
}
