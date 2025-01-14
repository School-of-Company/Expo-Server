package team.startup.expo.domain.form.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.form.presentation.dto.request.FormRequestDto;
import team.startup.expo.domain.form.service.CreateFormService;
import team.startup.expo.domain.form.service.UpdateFormService;

@RestController
@RequestMapping("/form")
@RequiredArgsConstructor
public class FormController {

    private final CreateFormService createFormService;
    private final UpdateFormService updateFormService;

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
}
