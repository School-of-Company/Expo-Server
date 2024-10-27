package team.startup.expo.domain.expo.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.expo.presentation.dto.request.GenerateExpoRequestDto;
import team.startup.expo.domain.expo.presentation.dto.request.UpdateExpoRequestDto;
import team.startup.expo.domain.expo.service.GenerateExpoService;
import team.startup.expo.domain.expo.service.UpdateExpoService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expo")
public class ExpoController {

    private final GenerateExpoService generateExpoService;
    private final UpdateExpoService updateExpoService;

    @PostMapping
    public ResponseEntity<Void> generateExpo(@RequestBody GenerateExpoRequestDto dto) {
        generateExpoService.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{expo_id}")
    public ResponseEntity<Void> updateExpo(@PathVariable("expo_id") Long expoId, @RequestBody UpdateExpoRequestDto dto) {
        updateExpoService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
