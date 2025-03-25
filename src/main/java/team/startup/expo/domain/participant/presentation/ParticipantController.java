package team.startup.expo.domain.participant.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.participant.presentation.dto.response.GetParticipantInfoResponseDto;
import team.startup.expo.domain.participant.service.GetParticipantInfoService;
import team.startup.expo.domain.trainee.entity.ApplicationType;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participant")
public class ParticipantController {

    private final GetParticipantInfoService getParticipantInfoService;

    @GetMapping("/{expo_id}")
    public ResponseEntity<Page<GetParticipantInfoResponseDto>> getParticipantInfo(
            @PathVariable("expo_id") String expoId,
            @RequestParam(value = "type") ApplicationType type,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "size", defaultValue = "7") int size,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        PageRequest pageable = PageRequest.of(page, size);

        Page<GetParticipantInfoResponseDto> response = getParticipantInfoService.execute(expoId, type, name, pageable, date);
        return ResponseEntity.ok(response);
    }
}
