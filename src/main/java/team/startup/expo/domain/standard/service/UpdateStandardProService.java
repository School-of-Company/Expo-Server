package team.startup.expo.domain.standard.service;

import team.startup.expo.domain.standard.presentation.dto.request.UpdateStandardProRequestDto;

public interface UpdateStandardProService {
    void execute(Long standardProId, UpdateStandardProRequestDto dto);
}
