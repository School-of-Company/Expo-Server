package team.startup.expo.domain.attendance.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;
import team.startup.expo.domain.form.entity.ParticipationType;

@Getter
@Builder
public class PreEnterScanQrCodeResponseDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private Boolean personalInformationStatus;
    private ParticipationType participationType;
}
