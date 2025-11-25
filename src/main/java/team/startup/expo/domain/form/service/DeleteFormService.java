package team.startup.expo.domain.form.service;

import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.trainee.entity.ApplicationType;

public interface DeleteFormService {
    void execute(String expoId, ParticipationType participationType, ApplicationType applicationType);
}
