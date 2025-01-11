package team.startup.expo.domain.participant.service;

import team.startup.expo.domain.participant.presentation.dto.response.GetParticipantInfoResponseDto;
import team.startup.expo.domain.trainee.ApplicationType;

import java.util.List;

public interface GetParticipantInfoService {
    List<GetParticipantInfoResponseDto> execute(String expoId, ApplicationType type);
}
