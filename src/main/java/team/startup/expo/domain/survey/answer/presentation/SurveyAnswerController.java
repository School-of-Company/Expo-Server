package team.startup.expo.domain.survey.answer.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.survey.answer.presentation.dto.request.SurveyAnswerRequestDto;
import team.startup.expo.domain.survey.answer.service.StandardSurveyAnswerService;
import team.startup.expo.domain.survey.answer.service.TraineeSurveyAnswerService;

@RestController
@RequestMapping("/survey/answer")
@RequiredArgsConstructor
public class SurveyAnswerController {

    private final StandardSurveyAnswerService standardSurveyAnswerService;
    private final TraineeSurveyAnswerService traineeSurveyAnswerService;

    @PostMapping("/standard/{expo_id}")
    public ResponseEntity<Void> standardSurveyAnswer(@PathVariable("expo_id") String expoId, @Valid @RequestBody SurveyAnswerRequestDto dto) {
        standardSurveyAnswerService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/trainee/{expo_id}")
    public ResponseEntity<Void> traineeSurveyAnswer(@PathVariable("expo_id") String expoId, @Valid @RequestBody SurveyAnswerRequestDto dto) {
        traineeSurveyAnswerService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
