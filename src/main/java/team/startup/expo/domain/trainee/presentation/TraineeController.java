package team.startup.expo.domain.trainee.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.trainee.presentation.dto.response.GetTraineeInformationResponseDto;
import team.startup.expo.domain.trainee.service.GetTrainingInformationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainee")
public class TraineeController {

    private final GetTrainingInformationService getTrainingInformationService;

    @GetMapping("/{expo_id}")
    public ResponseEntity<List<GetTraineeInformationResponseDto>> getTraineeInformation(@PathVariable("expo_id") String expoId, @RequestParam(required = false) String name) {
        List<GetTraineeInformationResponseDto> result = getTrainingInformationService.execute(expoId, name);
        return ResponseEntity.ok(result);
    }
}
