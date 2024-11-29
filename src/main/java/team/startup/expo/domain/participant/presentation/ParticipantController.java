package team.startup.expo.domain.participant.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.participant.presentation.dto.response.GetParticipantInfoResponseDto;
import team.startup.expo.domain.participant.service.GetParticipantInfoService;
import team.startup.expo.domain.trainee.ParticipationType;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participant")
public class ParticipantController {

    private final GetParticipantInfoService getParticipantInfoService;

    @GetMapping("/{expo_id}")
    public ResponseEntity<List<GetParticipantInfoResponseDto>> getParticipantInfo(@PathVariable("expo_id") String expoId, @RequestParam("type") ParticipationType type) {
        List<GetParticipantInfoResponseDto> response = getParticipantInfoService.execute(expoId, type);
        return ResponseEntity.ok(response);
    }
}
