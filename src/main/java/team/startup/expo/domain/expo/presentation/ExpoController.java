package team.startup.expo.domain.expo.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.startup.expo.domain.expo.presentation.dto.request.GenerateExpoRequestDto;
import team.startup.expo.domain.expo.service.GenerateExpoService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expo")
public class ExpoController {

    private final GenerateExpoService generateExpoService;

    @PostMapping
    public ResponseEntity<Void> generateExpo(@RequestBody GenerateExpoRequestDto dto) {
        generateExpoService.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
