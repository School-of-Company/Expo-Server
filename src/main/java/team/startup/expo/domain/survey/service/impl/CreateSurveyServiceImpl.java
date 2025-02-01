package team.startup.expo.domain.survey.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.survey.entity.DynamicSurvey;
import team.startup.expo.domain.survey.entity.Survey;
import team.startup.expo.domain.survey.exception.AlreadyExistSurveyException;
import team.startup.expo.domain.survey.presentation.dto.request.SurveyRequestDto;
import team.startup.expo.domain.survey.repository.DynamicSurveyRepository;
import team.startup.expo.domain.survey.repository.SurveyRepository;
import team.startup.expo.domain.survey.service.CreateSurveyService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class CreateSurveyServiceImpl implements CreateSurveyService {

    private final SurveyRepository surveyRepository;
    private final DynamicSurveyRepository dynamicSurveyRepository;
    private final ExpoRepository expoRepository;

    public void execute(String expoId, SurveyRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (surveyRepository.existsByExpoAndParticipationType(expo, dto.getParticipationType()))
            throw new AlreadyExistSurveyException();

        Survey survey = saveSurvey(dto, expo);

        dto.getDynamicSurveyRequestDto().forEach(dynamicSurveyRequestDto -> saveDynamicSurvey(dynamicSurveyRequestDto, survey));
    }

    private Survey saveSurvey(SurveyRequestDto dto, Expo expo) {
        Survey survey = Survey.builder()
                .participationType(dto.getParticipationType())
                .expo(expo)
                .build();

        return surveyRepository.save(survey);
    }

    private void saveDynamicSurvey(SurveyRequestDto.DynamicSurveyRequestDto dto, Survey survey) {
        DynamicSurvey dynamicSurvey = DynamicSurvey.builder()
                .title(dto.getTitle())
                .jsonData(dto.getJsonData())
                .formType(dto.getFormType())
                .requiredStatus(dto.getRequiredStatus())
                .otherJson(dto.getOtherJson())
                .survey(survey)
                .build();

        dynamicSurveyRepository.save(dynamicSurvey);
    }
}
