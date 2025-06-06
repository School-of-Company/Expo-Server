package team.startup.expo.domain.standard.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.standard.presentation.dto.request.AddStandardProRequestDto;
import team.startup.expo.domain.standard.presentation.dto.request.ApplicationStandardProListRequestDto;
import team.startup.expo.domain.standard.presentation.dto.request.UpdateStandardProRequestDto;
import team.startup.expo.domain.standard.presentation.dto.response.GetStandardProParticipantResponseDto;
import team.startup.expo.domain.standard.presentation.dto.response.GetStandardProgramResponseDto;
import team.startup.expo.domain.standard.service.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/standard")
public class StandardController {

    private final AddStandardProService addStandardProService;
    private final AddStandardProListService addStandardProListService;
    private final UpdateStandardProService updateStandardProService;
    private final DeleteStandardProService deleteStandardProService;
    private final GetStandardProListService getStandardProListService;
    private final GetParticipantByStandardProService getParticipantByStandardProService;
    private final ApplicationStandardProListService applicationStandardProListService;

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

    @GetMapping("/program/{expo_id}")
    public ResponseEntity<List<GetStandardProgramResponseDto>> getStandardProList(@PathVariable("expo_id") String expoId) {
        List<GetStandardProgramResponseDto> response = getStandardProListService.execute(expoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{standardPro_id}")
    public ResponseEntity<List<GetStandardProParticipantResponseDto>> getParticipantByStandardPro(@PathVariable("standardPro_id") Long standardProId) {
        List<GetStandardProParticipantResponseDto> response = getParticipantByStandardProService.execute(standardProId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/application/{expo_id}")
    public ResponseEntity<Void> applicationStandardProList(@PathVariable("expo_id") String expoId, @RequestBody @Valid ApplicationStandardProListRequestDto dto) {
        applicationStandardProListService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
