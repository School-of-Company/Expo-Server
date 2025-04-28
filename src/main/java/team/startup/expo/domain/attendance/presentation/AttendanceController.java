package team.startup.expo.domain.attendance.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.attendance.presentation.dto.request.PreEnterScanQrCodeRequestDto;
import team.startup.expo.domain.attendance.presentation.dto.request.ScanStandardProRequestDto;
import team.startup.expo.domain.attendance.presentation.dto.request.ScanTrainingProRequestDto;
import team.startup.expo.domain.attendance.presentation.dto.response.PreEnterScanQrCodeResponseDto;
import team.startup.expo.domain.attendance.service.PreEnterScanQrCodeService;
import team.startup.expo.domain.attendance.service.ScanStandardProByQrCodeService;
import team.startup.expo.domain.attendance.service.ScanTrainingProByQrCodeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {

    private final ScanTrainingProByQrCodeService scanTrainingProByQrCodeService;
    private final ScanStandardProByQrCodeService scanStandardProByQrCodeService;
    private final PreEnterScanQrCodeService preEnterScanQrCodeService;

    @PatchMapping("/training/{trainingPro_id}")
    public ResponseEntity<Void> scanTrainingProByQrCode(
        @PathVariable("trainingPro_id") Long trainingProId,
        @RequestBody @Valid ScanTrainingProRequestDto dto
    ) {
        scanTrainingProByQrCodeService.execute(trainingProId, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/standard/{standardPro_id}")
    public ResponseEntity<Void> scanStandardProByQrCode(
        @PathVariable("standardPro_id") Long standardProId,
        @RequestBody @Valid ScanStandardProRequestDto dto
    ) {
        scanStandardProByQrCodeService.execute(standardProId, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{expo_id}")
    public ResponseEntity<PreEnterScanQrCodeResponseDto> preEnterScanQrCode(
        @PathVariable("expo_id") String expoId,
        @RequestBody @Valid PreEnterScanQrCodeRequestDto dto
    ) {
        PreEnterScanQrCodeResponseDto response = preEnterScanQrCodeService.execute(expoId, dto);
        return ResponseEntity.ok(response);
    }
}
