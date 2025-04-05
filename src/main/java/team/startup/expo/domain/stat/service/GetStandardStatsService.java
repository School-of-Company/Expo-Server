package team.startup.expo.domain.stat.service;

import team.startup.expo.domain.stat.presentation.dto.response.StandardParticipantStatsResponse;

public interface GetStandardStatsService {
    StandardParticipantStatsResponse execute(String expoId);
}
