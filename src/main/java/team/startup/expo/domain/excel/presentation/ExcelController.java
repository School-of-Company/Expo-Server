package team.startup.expo.domain.excel.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.excel.service.ProgramParticipantInfoToExcelService;
import team.startup.expo.domain.excel.service.StandardParticipantInfoToExcelService;
import team.startup.expo.domain.excel.service.TraineeInfoToExcelService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExcelController {

    private final TraineeInfoToExcelService traineeInfoToExcelService;
    private final StandardParticipantInfoToExcelService standardParticipantInfoToExcelService;
    private final ProgramParticipantInfoToExcelService programParticipantInfoToExcelService;

    @GetMapping("/{expo_id}")
    public ResponseEntity<Void> traineeInfoToExcel(
        @PathVariable("expo_id") String expoId,
        HttpServletResponse res
    ) throws JsonProcessingException {
        traineeInfoToExcelService.execute(expoId, res);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/standard/{expo_id}")
    public ResponseEntity<Void> standardParticipantInfoToExcel(
        @PathVariable("expo_id") String expoId,
        HttpServletResponse res
    ) throws JsonProcessingException {
        standardParticipantInfoToExcelService.execute(expoId, res);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/program/{expo_id}")
    public ResponseEntity<Void> programParticipantInfoToExcel(
        @PathVariable("expo_id") String expoId,
        @RequestParam("programId") Long programId,
        HttpServletResponse res
    ) throws JsonProcessingException {
        programParticipantInfoToExcelService.execute(expoId, programId, res);
        return ResponseEntity.ok().build();
    }
}
