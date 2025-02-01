package team.startup.expo.domain.form.service;

import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.form.presentation.dto.response.GetFormResponseDto;

public interface GetFormService {
    GetFormResponseDto execute(String expoId, ParticipationType participationType);
}
