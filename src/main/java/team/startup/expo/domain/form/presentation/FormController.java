package team.startup.expo.domain.form.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.form.presentation.dto.request.PreApplicationForTraineeRequestDto;
import team.startup.expo.domain.form.service.PreApplicationForTraineeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/form")
public class FormController {

    private final PreApplicationForTraineeService preApplicationForTraineeService;

    @PostMapping("/{expo_id}")
    public ResponseEntity<Void> preApplicationForTrainee(@PathVariable("expo_id") String expoId, @RequestBody @Valid PreApplicationForTraineeRequestDto dto) {
        preApplicationForTraineeService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
