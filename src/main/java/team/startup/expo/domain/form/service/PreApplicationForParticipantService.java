package team.startup.expo.domain.form.service;

import team.startup.expo.domain.form.presentation.dto.request.ApplicationForParticipantRequestDto;

public interface PreApplicationForParticipantService {
    void execute(String expoId, ApplicationForParticipantRequestDto dto);
}
