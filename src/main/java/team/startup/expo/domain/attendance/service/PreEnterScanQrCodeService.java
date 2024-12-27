package team.startup.expo.domain.attendance.service;

import team.startup.expo.domain.attendance.presentation.dto.request.PreEnterScanQrCodeRequestDto;
import team.startup.expo.domain.attendance.presentation.dto.response.PreEnterScanQrCodeResponseDto;

public interface PreEnterScanQrCodeService {
    PreEnterScanQrCodeResponseDto execute(String expoId, PreEnterScanQrCodeRequestDto dto);
}
