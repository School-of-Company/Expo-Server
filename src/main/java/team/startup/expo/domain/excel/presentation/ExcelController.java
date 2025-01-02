package team.startup.expo.domain.excel.presentation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.startup.expo.domain.excel.service.TraineeInfoToExcelService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExcelController {

    private final TraineeInfoToExcelService traineeInfoToExcelService;

    @GetMapping("/{expo_id}")
    public ResponseEntity<Void> traineeInfoToExcel(@PathVariable("expo_id") String expoId, HttpServletResponse res) {
        traineeInfoToExcelService.execute(expoId, res);
        return ResponseEntity.ok().build();
    }
}
