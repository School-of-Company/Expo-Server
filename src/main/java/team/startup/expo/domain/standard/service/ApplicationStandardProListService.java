package team.startup.expo.domain.standard.service;

import team.startup.expo.domain.standard.presentation.dto.request.ApplicationStandardProListRequestDto;

public interface ApplicationStandardProListService {
    void execute(String expoId, ApplicationStandardProListRequestDto dto);
}
