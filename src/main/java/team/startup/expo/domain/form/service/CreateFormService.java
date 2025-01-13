package team.startup.expo.domain.form.service;

import team.startup.expo.domain.form.presentation.dto.request.CreateFormRequestDto;

public interface CreateFormService {
    void execute(String expoId, CreateFormRequestDto createFormRequestDto);
}
