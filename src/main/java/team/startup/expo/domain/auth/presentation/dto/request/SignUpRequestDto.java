package team.startup.expo.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SignUpRequestDto {
    @NotNull
    private String name;

    @NotNull
    private String nickname;

    @NotNull
    @Pattern(regexp = ".+@.+", message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,24}$",
            message = "비밀번호는 8~24자이며, 대문자, 소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
    )
    @NotNull
    private String password;

    @NotNull
    private String phoneNumber;
}
