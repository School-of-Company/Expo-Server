package team.startup.expo.domain.attendance.service;

import team.startup.expo.domain.attendance.presentation.dto.request.PreEnterScanQrCodeRequestDto;

public interface PreEnterScanQrCodeService {
    void execute(PreEnterScanQrCodeRequestDto dto);
}
