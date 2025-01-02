package team.startup.expo.domain.auth.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.auth.presentation.dto.request.SignInRequestDto;
import team.startup.expo.domain.auth.presentation.dto.request.SignUpRequestDto;
import team.startup.expo.domain.auth.presentation.dto.response.TokenResponseDto;
import team.startup.expo.domain.auth.service.LogoutService;
import team.startup.expo.domain.auth.service.ReissueTokenService;
import team.startup.expo.domain.auth.service.SignInService;
import team.startup.expo.domain.auth.service.SignUpService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final SignUpService signUpService;
    private final SignInService signInService;
    private final ReissueTokenService reissueTokenService;
    private final LogoutService logoutService;

    @PostMapping
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequestDto dto) {
        signUpService.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signin")
    public ResponseEntity<TokenResponseDto> signIn(@RequestBody SignInRequestDto dto) {
        TokenResponseDto result = signInService.execute(dto);
        return ResponseEntity.ok(result);
    }

    @PatchMapping
    public ResponseEntity<TokenResponseDto> reissueToken(@RequestHeader("RefreshToken") String refreshToken) {
        TokenResponseDto result = reissueTokenService.execute(refreshToken);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<Void> logout() {
        logoutService.execute();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
