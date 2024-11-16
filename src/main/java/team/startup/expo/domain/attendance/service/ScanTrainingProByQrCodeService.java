package team.startup.expo.domain.attendance.service;

import team.startup.expo.domain.attendance.presentation.dto.ScanTrainingProRequestDto;

public interface ScanTrainingProByQrCodeService {
    void execute(Long trainingProId, ScanTrainingProRequestDto dto);
}
