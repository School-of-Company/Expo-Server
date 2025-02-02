package team.startup.expo.domain.survey.management.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.survey.management.entity.DynamicSurvey;
import team.startup.expo.domain.survey.management.entity.Survey;
import team.startup.expo.domain.survey.management.exception.NotFoundSurveyException;
import team.startup.expo.domain.survey.management.presentation.dto.response.SurveyResponseDto;
import team.startup.expo.domain.survey.management.repository.DynamicSurveyRepository;
import team.startup.expo.domain.survey.management.repository.SurveyRepository;
import team.startup.expo.domain.survey.management.service.GetSurveyService;
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

        List<SurveyResponseDto.DynamicSurveyResponseDto> dynamicSurveyResponseDto = dynamicSurveyList.stream()
                .map(dynamicSurvey -> SurveyResponseDto.DynamicSurveyResponseDto.builder()
                        .title(dynamicSurvey.getTitle())
                        .jsonData(dynamicSurvey.getJsonData())
                        .formType(dynamicSurvey.getFormType())
                        .requiredStatus(dynamicSurvey.getRequiredStatus())
                        .otherJson(dynamicSurvey.getOtherJson())
                        .build()
                ).toList();

        return SurveyResponseDto.builder()
                .participationType(participationType)
                .dynamicSurveyResponseDto(dynamicSurveyResponseDto)
                .build();
    }
}
