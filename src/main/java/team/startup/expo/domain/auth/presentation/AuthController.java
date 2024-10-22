package team.startup.expo.domain.auth.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.startup.expo.domain.auth.presentation.dto.request.SignInRequestDto;
import team.startup.expo.domain.auth.presentation.dto.request.SignUpRequestDto;
import team.startup.expo.domain.auth.presentation.dto.response.TokenResponseDto;
import team.startup.expo.domain.auth.service.SignInService;
import team.startup.expo.domain.auth.service.SignUpService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final SignUpService signUpService;
    private final SignInService signInService;

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
}
