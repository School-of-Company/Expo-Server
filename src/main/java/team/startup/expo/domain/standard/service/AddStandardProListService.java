package team.startup.expo.domain.standard.service;

import team.startup.expo.domain.standard.presentation.dto.request.AddStandardProRequestDto;

import java.util.List;

public interface AddStandardProListService {
    void execute(Long expoId, List<AddStandardProRequestDto> dtos);
}
