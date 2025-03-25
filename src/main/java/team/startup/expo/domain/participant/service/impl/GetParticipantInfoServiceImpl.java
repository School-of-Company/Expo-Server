package team.startup.expo.domain.participant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.startup.expo.domain.participant.presentation.dto.response.GetParticipantInfoResponseDto;
import team.startup.expo.domain.participant.repository.custom.ParticipantRepositoryCustom;
import team.startup.expo.domain.participant.service.GetParticipantInfoService;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.time.LocalDate;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetParticipantInfoServiceImpl implements GetParticipantInfoService {

    private final ParticipantRepositoryCustom participantRepositoryCustom;

    public Page<GetParticipantInfoResponseDto> execute(
            String expoId, ApplicationType type, String name, Pageable pageable, LocalDate date) {
        return participantRepositoryCustom.searchParticipants(expoId, type, name, pageable, date);

    }
}
