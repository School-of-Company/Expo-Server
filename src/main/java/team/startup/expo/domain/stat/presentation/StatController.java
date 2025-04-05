package team.startup.expo.domain.stat.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.startup.expo.domain.stat.presentation.dto.response.StandardParticipantStatsResponse;
import team.startup.expo.domain.stat.service.GetStandardStatsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stat")
public class StatController {

    private final GetStandardStatsService getStandardStatsService;

    @GetMapping("/{expo_id}")
    public ResponseEntity<StandardParticipantStatsResponse> getStandardStats(
        @PathVariable("expo_id") String expoId
    ) {
        StandardParticipantStatsResponse response = getStandardStatsService.execute(expoId);
        return ResponseEntity.ok(response);
    }
}
