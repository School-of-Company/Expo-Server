package team.startup.expo.domain.standard.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.standard.presentation.dto.request.AddStandardProRequestDto;
import team.startup.expo.domain.standard.service.AddStandardProService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/standard")
public class StandardController {

    private final AddStandardProService addStandardProService;

    @PostMapping("/{expo_id}")
    public ResponseEntity<Void> addStandardPro(@PathVariable("expo_id") Long expoId, @RequestBody AddStandardProRequestDto dto) {
        addStandardProService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}