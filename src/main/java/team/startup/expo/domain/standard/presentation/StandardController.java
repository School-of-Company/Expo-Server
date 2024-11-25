package team.startup.expo.domain.standard.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.standard.presentation.dto.request.AddStandardProRequestDto;
import team.startup.expo.domain.standard.presentation.dto.request.UpdateStandardProRequestDto;
import team.startup.expo.domain.standard.service.AddStandardProListService;
import team.startup.expo.domain.standard.service.AddStandardProService;
import team.startup.expo.domain.standard.service.DeleteStandardProService;
import team.startup.expo.domain.standard.service.UpdateStandardProService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/standard")
public class StandardController {

    private final AddStandardProService addStandardProService;
    private final AddStandardProListService addStandardProListService;
    private final UpdateStandardProService updateStandardProService;
    private final DeleteStandardProService deleteStandardProService;

    @PostMapping("/{expo_id}")
    public ResponseEntity<Void> addStandardPro(@PathVariable("expo_id") String expoId, @RequestBody @Valid AddStandardProRequestDto dto) {
        addStandardProService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/list/{expo_id}")
    public ResponseEntity<Void> addStandardProList(@PathVariable("expo_id") String expoId, @RequestBody @Valid List<AddStandardProRequestDto> dtos) {
        addStandardProListService.execute(expoId, dtos);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{standardPro_id}")
    public ResponseEntity<Void> updateStandardPro(@PathVariable("standardPro_id") Long standardProId, @RequestBody @Valid UpdateStandardProRequestDto dto) {
        updateStandardProService.execute(standardProId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{standardPro_id}")
    public ResponseEntity<Void> deleteStandardPro(@PathVariable("standardPro_id") Long standardProId) {
        deleteStandardProService.execute(standardProId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
