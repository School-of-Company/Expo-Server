package team.startup.expo.domain.form.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.form.presentation.dto.GetFormResponseDto;
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

    @PatchMapping("/{form_id}")
    public ResponseEntity<Void> updateForm(@PathVariable("form_id") Long formId, @RequestBody @Valid FormRequestDto dto) {
        updateFormService.execute(formId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{form_id}")
    public ResponseEntity<Void> deleteForm(@PathVariable("form_id") Long formId) {
        deleteFormService.execute(formId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{expo_id}")
    public ResponseEntity<GetFormResponseDto> getForm(@PathVariable("expo_id") String expoId, @RequestParam("type") ParticipationType participationType) {
        GetFormResponseDto result = getFormService.execute(expoId, participationType);
        return ResponseEntity.ok(result);
    }
}
