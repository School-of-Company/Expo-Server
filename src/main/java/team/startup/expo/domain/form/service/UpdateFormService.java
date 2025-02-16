package team.startup.expo.domain.form.service;

import team.startup.expo.domain.form.presentation.dto.request.FormRequestDto;

public interface UpdateFormService {
    void execute(String expoId, FormRequestDto dto);
}
