package team.startup.expo.domain.auth.service;

import team.startup.expo.domain.auth.presentation.dto.request.SignInRequestDto;
import team.startup.expo.domain.auth.presentation.dto.response.TokenResponseDto;

public interface SignInService {
    TokenResponseDto execute(SignInRequestDto dto);
}
