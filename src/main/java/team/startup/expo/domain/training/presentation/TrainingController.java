package team.startup.expo.domain.training.presentation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.startup.expo.domain.training.presentation.dto.response.GetTrainingProTraineeResponseDto;
import team.startup.expo.domain.training.service.GetTraineeByTrainingProService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/training")
public class TrainingController {

    private final GetTraineeByTrainingProService getTraineeByTrainingProService;

    @GetMapping("/{trainingPro_id}")
    public ResponseEntity<List<GetTrainingProTraineeResponseDto>> getTraineeByTrainingPro(@PathVariable("trainingPro_id") Long trainingProId) {
        List<GetTrainingProTraineeResponseDto> result = getTraineeByTrainingProService.execute(trainingProId);
        return ResponseEntity.ok(result);
    }
}
