package team.startup.expo.domain.survey.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.survey.presentation.dto.request.SurveyRequestDto;
import team.startup.expo.domain.survey.service.CreateSurveyService;

@RestController
@RequestMapping("/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final CreateSurveyService createSurveyService;

    @PostMapping("/{expo_id}")
    public ResponseEntity<Void> createSurvey(@PathVariable("expo_id") String expoId, @Valid @RequestBody SurveyRequestDto dto) {
        createSurveyService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
