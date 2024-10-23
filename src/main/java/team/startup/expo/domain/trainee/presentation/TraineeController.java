package team.startup.expo.domain.trainee.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.startup.expo.domain.trainee.presentation.dto.response.GetTraineeInformationResponseDto;
import team.startup.expo.domain.trainee.service.GetTrainingInformationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainee")
public class TraineeController {

    private final GetTrainingInformationService getTrainingInformationService;

    @GetMapping
    public ResponseEntity<List<GetTraineeInformationResponseDto>> getTraineeInformation() {
        List<GetTraineeInformationResponseDto> result = getTrainingInformationService.execute();
        return ResponseEntity.ok(result);
    }
}
