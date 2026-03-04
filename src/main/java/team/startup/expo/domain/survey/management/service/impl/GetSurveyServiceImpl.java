package team.startup.expo.domain.survey.management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.survey.management.entity.Survey;
import team.startup.expo.domain.survey.management.exception.NotFoundSurveyException;
import team.startup.expo.domain.survey.management.presentation.dto.response.SurveyResponseDto;
import team.startup.expo.domain.survey.management.repository.DynamicSurveyRepository;
import team.startup.expo.domain.survey.management.repository.projection.DynamicSurveyDto;
import team.startup.expo.domain.survey.management.repository.SurveyRepository;
import team.startup.expo.domain.survey.management.service.GetSurveyService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
@CacheConfig(cacheNames = "Survey")
public class GetSurveyServiceImpl implements GetSurveyService {

    private final SurveyRepository surveyRepository;
    private final DynamicSurveyRepository dynamicSurveyRepository;
    private final ExpoRepository expoRepository;

    @Cacheable(key = "#expoId + '_' + #participationType", cacheManager = "cacheManager")
    public SurveyResponseDto execute(String expoId, ParticipationType participationType) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        Survey survey = surveyRepository.findByExpoAndParticipationType(expo, participationType)
                .orElseThrow(NotFoundSurveyException::new);

        List<DynamicSurveyDto> dynamicSurveyDtos = dynamicSurveyRepository.findAllBySurveyIdWithJson(survey.getId());

        List<SurveyResponseDto.DynamicSurveyResponseDto> dynamicSurveyResponseDto = dynamicSurveyDtos.stream()
                .map(dynamicSurveyDto -> SurveyResponseDto.DynamicSurveyResponseDto.builder()
                        .title(dynamicSurveyDto.title())
                        .jsonData(dynamicSurveyDto.jsonData())
                        .formType(dynamicSurveyDto.formType())
                        .requiredStatus(dynamicSurveyDto.requiredStatus())
                        .otherJson(dynamicSurveyDto.otherJson())
                        .build())
                .toList();

        return SurveyResponseDto.builder()
                .informationText(survey.getInformationText())
                .participationType(participationType)
                .title(survey.getTitle())
                .dynamicSurveyResponseDto(dynamicSurveyResponseDto)
                .build();
    }
}
