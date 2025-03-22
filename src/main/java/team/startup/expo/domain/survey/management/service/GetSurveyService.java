package team.startup.expo.domain.survey.management.service;

import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.survey.management.presentation.dto.response.SurveyResponseDto;

public interface GetSurveyService {
    SurveyResponseDto execute(String expoId, ParticipationType participationType);
}
