package team.startup.expo.domain.participant.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.startup.expo.domain.participant.presentation.dto.response.GetParticipantInfoResponseDto;
import team.startup.expo.domain.trainee.entity.ApplicationType;

import java.time.LocalDate;

public interface ParticipantRepositoryCustom {
    Page<GetParticipantInfoResponseDto> searchParticipants(String expoId, ApplicationType type, String name, Pageable pageable, LocalDate date);
}
