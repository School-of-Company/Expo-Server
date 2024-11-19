package team.startup.expo.domain.attendance.service;

import team.startup.expo.domain.attendance.presentation.dto.request.ScanStandardProRequestDto;

public interface ScanStandardProByQrCodeService {
    void execute(Long standardProId, ScanStandardProRequestDto dto);
}
