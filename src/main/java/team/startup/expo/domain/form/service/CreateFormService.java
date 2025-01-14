package team.startup.expo.domain.form.service;

import team.startup.expo.domain.form.presentation.dto.request.FormRequestDto;

public interface CreateFormService {
    void execute(String expoId, FormRequestDto formRequestDto);
}
