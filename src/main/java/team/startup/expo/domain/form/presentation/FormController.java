package team.startup.expo.domain.form.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.form.presentation.dto.request.PreApplicationForParticipantRequestDto;
import team.startup.expo.domain.form.presentation.dto.request.PreApplicationForTraineeRequestDto;
import team.startup.expo.domain.form.service.PreApplicationForParticipantService;
import team.startup.expo.domain.form.service.PreApplicationForTraineeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/form")
public class FormController {

    private final PreApplicationForTraineeService preApplicationForTraineeService;
    private final PreApplicationForParticipantService preApplicationForParticipantService;

    @PostMapping("/{expo_id}")
    public ResponseEntity<Void> preApplicationForTrainee(@PathVariable("expo_id") String expoId, @RequestBody @Valid PreApplicationForTraineeRequestDto dto) {
        preApplicationForTraineeService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/pre-standard/{expo_id}")
    public ResponseEntity<Void> preApplicationForParticipant(@PathVariable("expo_id") String expoId, @RequestBody @Valid PreApplicationForParticipantRequestDto dto) {
        preApplicationForParticipantService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
