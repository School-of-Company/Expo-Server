package team.startup.expo.domain.form.service;

import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.form.presentation.dto.response.GetFormResponseDto;
import team.startup.expo.domain.trainee.entity.ApplicationType;

public interface GetFormService {
    GetFormResponseDto execute(String expoId, ParticipationType participationType, ApplicationType registrationType);
}
