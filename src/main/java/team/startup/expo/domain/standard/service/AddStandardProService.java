package team.startup.expo.domain.standard.service;

import team.startup.expo.domain.standard.presentation.dto.request.AddStandardProRequestDto;

public interface AddStandardProService {
    void execute(Long expoId, AddStandardProRequestDto dto);
}
