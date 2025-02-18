package team.startup.expo.domain.form.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.form.presentation.dto.response.GetFormResponseDto;
import team.startup.expo.domain.form.presentation.dto.request.FormRequestDto;
import team.startup.expo.domain.form.service.CreateFormService;
import team.startup.expo.domain.form.service.DeleteFormService;
import team.startup.expo.domain.form.service.GetFormService;
import team.startup.expo.domain.form.service.UpdateFormService;

@RestController
@RequestMapping("/form")
@RequiredArgsConstructor
public class FormController {

    private final CreateFormService createFormService;
    private final UpdateFormService updateFormService;
    private final DeleteFormService deleteFormService;
    private final GetFormService getFormService;

    @PostMapping("/{expo_id}")
    public ResponseEntity<Void> createForm(@PathVariable("expo_id") String expoId, @RequestBody @Valid FormRequestDto dto) {
        createFormService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{expo_id}")
    public ResponseEntity<Void> updateForm(@PathVariable("expo_id") String expoId, @RequestBody @Valid FormRequestDto dto) {
        updateFormService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{expo_id}/{participationType}")
    public ResponseEntity<Void> deleteForm(@PathVariable("expo_id") String expoId, @PathVariable ParticipationType participationType) {
        deleteFormService.execute(expoId, participationType);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{expo_id}")
    public ResponseEntity<GetFormResponseDto> getForm(@PathVariable("expo_id") String expoId, @RequestParam("type") ParticipationType participationType) {
        GetFormResponseDto result = getFormService.execute(expoId, participationType);
        return ResponseEntity.ok(result);
    }
}
