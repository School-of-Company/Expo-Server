package team.startup.expo.domain.attendance.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PreEnterScanQrCodeResponseDto {
    private String name;
    private String phoneNumber;
    private Boolean personalInformationStatus;
}
