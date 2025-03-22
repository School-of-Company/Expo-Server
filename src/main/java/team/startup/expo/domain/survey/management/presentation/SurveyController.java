package team.startup.expo.domain.survey.management.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.survey.management.presentation.dto.request.SurveyRequestDto;
import team.startup.expo.domain.survey.management.presentation.dto.response.SurveyResponseDto;
import team.startup.expo.domain.survey.management.service.CreateSurveyService;
import team.startup.expo.domain.survey.management.service.DeleteSurveyService;
import team.startup.expo.domain.survey.management.service.GetSurveyService;
import team.startup.expo.domain.survey.management.service.UpdateSurveyService;

@RestController
@RequestMapping("/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final CreateSurveyService createSurveyService;
    private final GetSurveyService getSurveyService;
    private final UpdateSurveyService updateSurveyService;
    private final DeleteSurveyService deleteSurveyService;

    @PostMapping("/{expo_id}")
    public ResponseEntity<Void> createSurvey(@PathVariable("expo_id") String expoId, @Valid @RequestBody SurveyRequestDto dto) {
        createSurveyService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{expo_id}")
    public ResponseEntity<SurveyResponseDto> getSurvey(@PathVariable("expo_id") String expoId, @RequestParam("type")ParticipationType participationType) {
        SurveyResponseDto response = getSurveyService.execute(expoId, participationType);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{expo_id}")
    public ResponseEntity<Void> updateSurvey(@PathVariable("expo_id") String expoId, @Valid @RequestBody SurveyRequestDto dto) {
        updateSurveyService.execute(expoId, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{expo_id}/{participationType}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable("expo_id") String expoId, @PathVariable ParticipationType participationType) {
        deleteSurveyService.execute(expoId, participationType);
        return ResponseEntity.noContent().build();
    }
}
