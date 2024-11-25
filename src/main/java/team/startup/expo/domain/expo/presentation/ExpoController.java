package team.startup.expo.domain.expo.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.expo.presentation.dto.request.GenerateExpoRequestDto;
import team.startup.expo.domain.expo.presentation.dto.request.UpdateExpoRequestDto;
import team.startup.expo.domain.expo.presentation.dto.response.GenerateExpoResponseDto;
import team.startup.expo.domain.expo.presentation.dto.response.GetExpoInfoResponseDto;
import team.startup.expo.domain.expo.presentation.dto.response.GetExpoResponseDto;
import team.startup.expo.domain.expo.service.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expo")
public class ExpoController {

    private final GenerateExpoService generateExpoService;
    private final UpdateExpoService updateExpoService;
    private final DeleteExpoService deleteExpoService;
    private final GetExpoInfoService getExpoInfoService;
    private final GetExpoListService getExpoListService;

    @PostMapping
    public ResponseEntity<GenerateExpoResponseDto> generateExpo(@RequestBody @Valid GenerateExpoRequestDto dto) {
        generateExpoService.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{expo_id}")
    public ResponseEntity<Void> updateExpo(@PathVariable("expo_id") String expoId, @RequestBody @Valid UpdateExpoRequestDto dto) {
        updateExpoService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{expo_id}")
    public ResponseEntity<Void> deleteExpo(@PathVariable("expo_id") String expoId) {
        deleteExpoService.execute(expoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{expo_id}")
    public ResponseEntity<GetExpoInfoResponseDto> getExpoInfo(@PathVariable("expo_id") String expoId) {
        GetExpoInfoResponseDto response = getExpoInfoService.execute(expoId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GetExpoResponseDto>> getExpoList() {
        List<GetExpoResponseDto> response = getExpoListService.execute();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
