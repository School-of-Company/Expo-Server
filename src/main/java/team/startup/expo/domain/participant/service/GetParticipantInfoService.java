package team.startup.expo.domain.participant.service;

import org.springframework.data.domain.Pageable;
import team.startup.expo.domain.participant.presentation.dto.response.ParticipantResponseDto;
import team.startup.expo.domain.trainee.entity.ApplicationType;

import java.time.LocalDate;

public interface GetParticipantInfoService {
    ParticipantResponseDto execute(String expoId, ApplicationType type, String name, Pageable pageable, LocalDate date);
}
