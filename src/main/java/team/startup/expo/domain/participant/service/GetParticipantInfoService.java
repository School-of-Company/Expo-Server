package team.startup.expo.domain.participant.service;

import org.springframework.data.domain.Pageable;
import team.startup.expo.domain.participant.presentation.dto.response.ParticipantResponseDto;

import java.time.LocalDate;

public interface GetParticipantInfoService {
    ParticipantResponseDto execute(String expoId, Pageable pageable, LocalDate date);
}
