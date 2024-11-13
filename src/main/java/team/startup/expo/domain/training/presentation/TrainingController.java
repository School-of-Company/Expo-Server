package team.startup.expo.domain.training.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.training.presentation.dto.request.AddTrainingProRequestDto;
import team.startup.expo.domain.training.presentation.dto.request.UpdateTrainingProRequestDto;
import team.startup.expo.domain.training.presentation.dto.response.GetTrainingProTraineeResponseDto;
import team.startup.expo.domain.training.service.AddTrainingProService;
import team.startup.expo.domain.training.service.DeleteTrainingProService;
import team.startup.expo.domain.training.service.GetTraineeByTrainingProService;
import team.startup.expo.domain.training.service.UpdateTrainingProService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/training")
public class TrainingController {

    private final GetTraineeByTrainingProService getTraineeByTrainingProService;
    private final AddTrainingProService addTrainingProService;
    private final UpdateTrainingProService updateTrainingProService;
    private final DeleteTrainingProService deleteTrainingProService;

    @GetMapping("/{trainingPro_id}")
    public ResponseEntity<List<GetTrainingProTraineeResponseDto>> getTraineeByTrainingPro(@PathVariable("trainingPro_id") Long trainingProId) {
        List<GetTrainingProTraineeResponseDto> result = getTraineeByTrainingProService.execute(trainingProId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{expo_id}")
    public ResponseEntity<Void> addTrainingPro(@PathVariable("expo_id") Long expoId, @RequestBody AddTrainingProRequestDto dto) {
        addTrainingProService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{trainingPro_id}")
    public ResponseEntity<Void> updateTrainingPro(@PathVariable("trainingPro_id") Long trainingProId, @RequestBody UpdateTrainingProRequestDto dto) {
        updateTrainingProService.execute(trainingProId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{trainingPro_id}")
    public ResponseEntity<Void> deleteTrainingPro(@PathVariable("trainingPro_id") Long trainingProId) {
        deleteTrainingProService.execute(trainingProId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
