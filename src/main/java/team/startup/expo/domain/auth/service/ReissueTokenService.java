package team.startup.expo.domain.auth.service;

import team.startup.expo.domain.auth.presentation.dto.response.TokenResponseDto;

public interface ReissueTokenService {
    TokenResponseDto execute(String refreshToken);
}
