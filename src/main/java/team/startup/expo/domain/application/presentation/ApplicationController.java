package team.startup.expo.domain.application.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.application.presentation.dto.request.ApplicationForParticipantRequestDto;
import team.startup.expo.domain.application.presentation.dto.request.ApplicationForTraineeRequestDto;
import team.startup.expo.domain.application.presentation.dto.request.ApplicationTemporaryQrRequestDto;
import team.startup.expo.domain.application.presentation.dto.response.ApplicationTemporaryQrResponseDto;
import team.startup.expo.domain.application.service.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/application")
public class ApplicationController {

    private final PreApplicationForTraineeService preApplicationForTraineeService;
    private final PreApplicationForParticipantService preApplicationForParticipantService;
    private final FieldApplicationForTraineeService fieldApplicationForTraineeService;
    private final FieldApplicationForParticipantService fieldApplicationForParticipantService;
    private final FieldApplicationTemporaryQrService fieldApplicationTemporaryQrService;

    @PostMapping("/{expo_id}")
    public ResponseEntity<Void> preApplicationForTrainee(
        @PathVariable("expo_id") String expoId,
        @RequestBody @Valid ApplicationForTraineeRequestDto dto
    ) {
        preApplicationForTraineeService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/pre-standard/{expo_id}")
    public ResponseEntity<Void> preApplicationForParticipant(
        @PathVariable("expo_id") String expoId,
        @RequestBody @Valid ApplicationForParticipantRequestDto dto
    ) {
        preApplicationForParticipantService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/field/{expo_id}")
    public ResponseEntity<Void> fieldApplicationForTrainee(
        @PathVariable("expo_id") String expoId,
        @RequestBody @Valid ApplicationForTraineeRequestDto dto
    ) {
        fieldApplicationForTraineeService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/field/standard/{expo_id}")
    public ResponseEntity<Void> fieldApplicationForParticipant(
        @PathVariable("expo_id") String expoId,
        @RequestBody @Valid ApplicationForParticipantRequestDto dto
    ) {
        fieldApplicationForParticipantService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/field/temporary/{expo_id}")
    public ResponseEntity<ApplicationTemporaryQrResponseDto> fieldApplicationTemporaryQr(
        @PathVariable("expo_id") String expoId,
        @RequestBody @Valid ApplicationTemporaryQrRequestDto dto
    ) {
        ApplicationTemporaryQrResponseDto response = fieldApplicationTemporaryQrService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
