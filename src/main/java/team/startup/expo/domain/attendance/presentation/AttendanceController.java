package team.startup.expo.domain.attendance.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.attendance.presentation.dto.ScanTrainingProRequestDto;
import team.startup.expo.domain.attendance.service.ScanTrainingProByQrCodeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {

    private final ScanTrainingProByQrCodeService scanTrainingProByQrCodeService;

    @PatchMapping("/training/{trainingPro_id}")
    public ResponseEntity<Void> scanTrainingProByQrCode(
        @PathVariable("trainingPro_id") Long trainingProId,
        @RequestBody ScanTrainingProRequestDto dto
    ) {
        scanTrainingProByQrCodeService.execute(trainingProId, dto);
        return ResponseEntity.ok().build();
    }
}
