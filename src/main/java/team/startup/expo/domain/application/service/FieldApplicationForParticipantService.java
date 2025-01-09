package team.startup.expo.domain.application.service;

import team.startup.expo.domain.application.presentation.dto.request.ApplicationForParticipantRequestDto;

public interface FieldApplicationForParticipantService {
    void execute(String expoId, ApplicationForParticipantRequestDto dto);
}
