package team.startup.expo.domain.auth.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.auth.presentation.dto.response.TokenResponseDto;
import team.startup.expo.domain.auth.repository.RefreshTokenRepository;
import team.startup.expo.domain.auth.service.ReissueTokenService;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.security.exception.ExpiredTokenException;
import team.startup.expo.global.security.jwt.JwtProvider;
import team.startup.expo.global.security.jwt.TokenParser;
import team.startup.expo.global.util.count.RefreshToken;

@TransactionService
@RequiredArgsConstructor
public class ReissueTokenServiceImpl implements ReissueTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final TokenParser tokenParser;

    public TokenResponseDto execute(String refreshToken) {
        String parseRefreshToken = tokenParser.parseRefreshToken(refreshToken);

        RefreshToken findRefreshToken = refreshTokenRepository.findByToken(parseRefreshToken)
                .orElseThrow(ExpiredTokenException::new);

        TokenResponseDto tokenResponseDto = jwtProvider.generateTokenDto(findRefreshToken.getEmail());

        saveRefreshToken(tokenResponseDto.getRefreshToken(), findRefreshToken.getEmail());

        return tokenResponseDto;
    }

    private void saveRefreshToken(String token, String email) {
        RefreshToken refreshToken = RefreshToken.builder()
                .email(email)
                .token(token)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}
