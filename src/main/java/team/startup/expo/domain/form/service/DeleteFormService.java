package team.startup.expo.domain.form.service;

import team.startup.expo.domain.form.entity.ParticipationType;

public interface DeleteFormService {
    void execute(String expoId, ParticipationType participationType);
}
