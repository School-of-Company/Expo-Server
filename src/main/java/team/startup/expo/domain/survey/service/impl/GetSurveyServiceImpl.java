package team.startup.expo.domain.survey.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.survey.entity.DynamicSurvey;
import team.startup.expo.domain.survey.entity.Survey;
import team.startup.expo.domain.survey.exception.NotFoundSurveyException;
import team.startup.expo.domain.survey.presentation.dto.response.SurveyResponseDto;
import team.startup.expo.domain.survey.repository.DynamicSurveyRepository;
import team.startup.expo.domain.survey.repository.SurveyRepository;
import team.startup.expo.domain.survey.service.GetSurveyService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetSurveyServiceImpl implements GetSurveyService {

    private final SurveyRepository surveyRepository;
    private final DynamicSurveyRepository dynamicSurveyRepository;
    private final ExpoRepository expoRepository;

    public SurveyResponseDto execute(String expoId, ParticipationType participationType) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        Survey survey = surveyRepository.findByExpoAndParticipationType(expo, participationType)
                .orElseThrow(NotFoundSurveyException::new);

        List<DynamicSurvey> dynamicSurveyList = dynamicSurveyRepository.findBySurvey(survey);

        List<SurveyResponseDto.DynamicSurveyRequestDto> dynamicSurveyRequestDto = dynamicSurveyList.stream()
                .map(dynamicSurvey -> SurveyResponseDto.DynamicSurveyRequestDto.builder()
                        .title(dynamicSurvey.getTitle())
                        .jsonData(dynamicSurvey.getJsonData())
                        .formType(dynamicSurvey.getFormType())
                        .requiredStatus(dynamicSurvey.getRequiredStatus())
                        .otherJson(dynamicSurvey.getOtherJson())
                        .build()
                ).toList();

        return SurveyResponseDto.builder()
                .participationType(participationType)
                .dynamicSurveyRequestDto(dynamicSurveyRequestDto)
                .build();
    }
}
