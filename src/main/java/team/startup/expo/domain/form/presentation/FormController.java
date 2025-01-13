package team.startup.expo.domain.form.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.form.presentation.dto.request.CreateFormRequestDto;
import team.startup.expo.domain.form.service.CreateFormService;

@RestController
@RequestMapping("/form")
@RequiredArgsConstructor
public class FormController {

    private final CreateFormService createFormService;

    @PostMapping("/{expo_id}")
    public ResponseEntity<Void> createForm(@PathVariable("expo_id") String expoId, @RequestBody @Valid CreateFormRequestDto dto) {
        createFormService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
