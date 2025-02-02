package team.startup.expo.domain.survey.answer.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.survey.answer.presentation.dto.request.StandardSurveyAnswerRequestDto;
import team.startup.expo.domain.survey.answer.service.StandardSurveyAnswerService;

@RestController
@RequestMapping("/survey/answer")
@RequiredArgsConstructor
public class SurveyAnswerController {

    private final StandardSurveyAnswerService standardSurveyAnswerService;

    @PostMapping("/standard/{expo_id}")
    public ResponseEntity<Void> standardSurveyAnswer(@PathVariable("expo_id") String expoId, @Valid @RequestBody StandardSurveyAnswerRequestDto dto) {
        standardSurveyAnswerService.execute(expoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
