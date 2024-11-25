package team.startup.expo.domain.standard.service;

import team.startup.expo.domain.standard.presentation.dto.response.GetStandardProParticipantResponseDto;

import java.util.List;

public interface GetParticipantByStandardProService {
    List<GetStandardProParticipantResponseDto> execute(Long standardProId);
}
