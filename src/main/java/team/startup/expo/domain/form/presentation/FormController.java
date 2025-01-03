package team.startup.expo.domain.form.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.form.presentation.dto.request.ApplicationForParticipantRequestDto;
import team.startup.expo.domain.form.presentation.dto.request.ApplicationForTraineeRequestDto;
import team.startup.expo.domain.form.service.FieldApplicationForParticipantService;
import team.startup.expo.domain.form.service.FieldApplicationForTraineeService;
import team.startup.expo.domain.form.service.PreApplicationForParticipantService;
import team.startup.expo.domain.form.service.PreApplicationForTraineeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/form")
public class FormController {

    private final PreApplicationForTraineeService preApplicationForTraineeService;
    private final PreApplicationForParticipantService preApplicationForParticipantService;
    private final FieldApplicationForTraineeService fieldApplicationForTraineeService;
    private final FieldApplicationForParticipantService fieldApplicationForParticipantService;

    @PostMapping("/{expo_id}")
    public ResponseEntity<Void> preApplicationForTrainee(@PathVariable("expo_id") String expoId, @RequestBody @Valid ApplicationForTraineeRequestDto dto) {
        preApplicationForTraineeService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/pre-standard/{expo_id}")
    public ResponseEntity<Void> preApplicationForParticipant(@PathVariable("expo_id") String expoId, @RequestBody @Valid ApplicationForParticipantRequestDto dto) {
        preApplicationForParticipantService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/field/{expo_id}")
    public ResponseEntity<Void> fieldApplicationForTrainee(@PathVariable("expo_id") String expoId, @RequestBody @Valid ApplicationForTraineeRequestDto dto) {
        fieldApplicationForTraineeService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/field/standard/{expo_id}")
    public ResponseEntity<Void> fieldApplicationForParticipant(@PathVariable("expo_id") String expoId, @RequestBody @Valid ApplicationForParticipantRequestDto dto) {
        fieldApplicationForParticipantService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
