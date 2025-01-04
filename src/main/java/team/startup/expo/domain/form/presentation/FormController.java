package team.startup.expo.domain.form.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.form.presentation.dto.request.CreateFormRequestDto;
import team.startup.expo.domain.form.service.CreateFormService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/form")
public class FormController {

    private final CreateFormService createFormService;

    @PostMapping("/{expo_id}")
    public ResponseEntity<Void> createForm(@PathVariable("expo_id") String expoId, @RequestBody List<CreateFormRequestDto> dtoList) {
        createFormService.execute(expoId, dtoList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
