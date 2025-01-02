package team.startup.expo.domain.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import team.startup.expo.domain.admin.Admin;
import team.startup.expo.domain.admin.Authority;
import team.startup.expo.domain.admin.exception.NotFoundUserException;
import team.startup.expo.domain.admin.repository.AdminRepository;
import team.startup.expo.domain.auth.exception.AdminStatePendingException;
import team.startup.expo.domain.auth.exception.NotMatchPasswordException;
import team.startup.expo.domain.auth.presentation.dto.request.SignInRequestDto;
import team.startup.expo.domain.auth.presentation.dto.response.TokenResponseDto;
import team.startup.expo.domain.auth.repository.RefreshTokenRepository;
import team.startup.expo.domain.auth.service.SignInService;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.security.jwt.JwtProvider;
import team.startup.expo.global.util.count.RefreshToken;

@TransactionService
@RequiredArgsConstructor
public class SignInServiceImpl implements SignInService {

    private final AdminRepository adminRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public TokenResponseDto execute(SignInRequestDto dto) {
        Admin admin = adminRepository.findByNickname(dto.getNickname())
                .orElseThrow(NotFoundUserException::new);

        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword()))
            throw new NotMatchPasswordException();

        if (admin.getAuthority() != Authority.ROLE_ADMIN)
            throw new AdminStatePendingException();

        TokenResponseDto tokenResponseDto = jwtProvider.generateTokenDto(admin.getEmail());

        saveRefreshToken(admin.getEmail(), tokenResponseDto.getRefreshToken());

        return tokenResponseDto;
    }

    private void saveRefreshToken(String email, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .email(email)
                .token(token)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}
