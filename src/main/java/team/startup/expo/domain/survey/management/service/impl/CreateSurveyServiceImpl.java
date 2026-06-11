package team.startup.expo.domain.survey.management.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.json.entity.DynamicJson;
import team.startup.expo.domain.json.entity.DynamicJsonType;
import team.startup.expo.domain.json.repository.DynamicJsonRepository;
import team.startup.expo.domain.survey.management.entity.DynamicSurvey;
import team.startup.expo.domain.survey.management.entity.Survey;
import team.startup.expo.domain.survey.management.exception.AlreadyExistSurveyException;
import team.startup.expo.domain.survey.management.presentation.dto.request.SurveyRequestDto;
import team.startup.expo.domain.survey.management.repository.DynamicSurveyRepository;
import team.startup.expo.domain.survey.management.repository.SurveyRepository;
import team.startup.expo.domain.survey.management.service.CreateSurveyService;
import team.startup.expo.global.annotation.TransactionService;

import java.util.ArrayList;
import java.util.List;

@TransactionService
@RequiredArgsConstructor
public class CreateSurveyServiceImpl implements CreateSurveyService {

    private final SurveyRepository surveyRepository;
    private final DynamicSurveyRepository dynamicSurveyRepository;
    private final ExpoRepository expoRepository;
    private final DynamicJsonRepository dynamicJsonRepository;

    public void execute(String expoId, SurveyRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (surveyRepository.existsByExpoAndParticipationType(expo, dto.getParticipationType()))
            throw new AlreadyExistSurveyException();

        Survey survey = saveSurvey(dto, expo);

        saveDynamicSurveys(dto.getDynamicSurveyRequestDto(), survey);
    }

    private Survey saveSurvey(SurveyRequestDto dto, Expo expo) {
        Survey survey = Survey.builder()
                .participationType(dto.getParticipationType())
                .informationText(dto.getInformationText())
                .expo(expo)
                .totalAnswers(0)
                .title(dto.getTitle())
                .build();

        return surveyRepository.save(survey);
    }

    private void saveDynamicSurveys(List<SurveyRequestDto.DynamicSurveyRequestDto> dtos, Survey survey) {
        List<DynamicSurvey> dynamicSurveys = dtos.stream()
                .map(dto -> DynamicSurvey.builder()
                        .survey(survey)
                        .title(dto.getTitle())
                        .formType(dto.getFormType())
                        .requiredStatus(dto.getRequiredStatus())
                        .build())
                .toList();

        List<DynamicSurvey> savedSurveys = dynamicSurveyRepository.saveAll(dynamicSurveys);

        List<DynamicJson> dynamicJsons = new ArrayList<>();
        for (int i = 0; i < savedSurveys.size(); i++) {
            SurveyRequestDto.DynamicSurveyRequestDto dto = dtos.get(i);
            dynamicJsons.add(DynamicJson.builder()
                    .dynamicJsonType(DynamicJsonType.SURVEY)
                    .recordId(savedSurveys.get(i).getId())
                    .jsonData(dto.getJsonData())
                    .otherJson(dto.getOtherJson())
                    .build());
        }

        dynamicJsonRepository.saveAll(dynamicJsons);
    }
}
