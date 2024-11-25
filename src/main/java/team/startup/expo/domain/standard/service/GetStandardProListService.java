package team.startup.expo.domain.standard.service;

import team.startup.expo.domain.standard.presentation.dto.response.GetStandardProgramResponseDto;

import java.util.List;

public interface GetStandardProListService {
    List<GetStandardProgramResponseDto> execute(String expoId);
}
