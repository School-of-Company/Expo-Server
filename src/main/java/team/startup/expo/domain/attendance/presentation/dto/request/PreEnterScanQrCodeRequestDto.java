package team.startup.expo.domain.attendance.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.admin.Authority;

@NoArgsConstructor
@Getter
public class PreEnterScanQrCodeRequestDto {
    @NotNull
    private Authority authority;

    @NotNull
    private String phoneNumber;
}
