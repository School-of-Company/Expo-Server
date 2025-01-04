package team.startup.expo.domain.form.service;

import team.startup.expo.domain.form.presentation.dto.request.CreateFormRequestDto;

import java.util.List;

public interface CreateFormService {
    void execute(String expoId, List<CreateFormRequestDto> dtoList);
}
