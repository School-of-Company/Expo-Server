package team.startup.expo.domain.form.service;

import team.startup.expo.domain.form.presentation.dto.request.PreApplicationForParticipantRequestDto;

public interface PreApplicationForParticipantService {
    void execute(String expoId, PreApplicationForParticipantRequestDto dto);
}
