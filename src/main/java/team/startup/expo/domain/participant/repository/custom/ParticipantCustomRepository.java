package team.startup.expo.domain.participant.repository.custom;

import org.springframework.data.domain.Pageable;
import team.startup.expo.domain.participant.presentation.dto.response.ParticipantResponseDto;

import java.time.LocalDate;

public interface ParticipantCustomRepository {
    ParticipantResponseDto searchParticipants(String expoId, Pageable pageable, LocalDate date);
}
