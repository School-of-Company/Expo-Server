package team.startup.expo.domain.survey.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.survey.presentation.dto.request.SurveyRequestDto;
import team.startup.expo.domain.survey.presentation.dto.response.SurveyResponseDto;
import team.startup.expo.domain.survey.service.CreateSurveyService;
import team.startup.expo.domain.survey.service.GetSurveyService;

@RestController
@RequestMapping("/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final CreateSurveyService createSurveyService;
    private final GetSurveyService getSurveyService;

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
}
