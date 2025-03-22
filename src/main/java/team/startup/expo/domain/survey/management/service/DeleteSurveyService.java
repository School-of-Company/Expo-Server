package team.startup.expo.domain.survey.management.service;

import team.startup.expo.domain.form.entity.ParticipationType;

public interface DeleteSurveyService {
    void execute(String expoId, ParticipationType participationType);
}
