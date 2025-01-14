package team.startup.expo.domain.form.service;

import team.startup.expo.domain.form.presentation.dto.GetFormResponseDto;

public interface GetFormService {
    GetFormResponseDto execute(Long formId);
}
