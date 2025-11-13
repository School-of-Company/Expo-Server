package team.startup.expo.domain.survey.management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.mongo.entity.DynamicSurveyJsonDoc;
import team.startup.expo.domain.mongo.repository.DynamicSurveyJsonRepository;
import team.startup.expo.domain.survey.management.entity.DynamicSurvey;
import team.startup.expo.domain.survey.management.entity.Survey;
import team.startup.expo.domain.survey.management.exception.NotFoundSurveyException;
import team.startup.expo.domain.survey.management.presentation.dto.response.SurveyResponseDto;
import team.startup.expo.domain.survey.management.repository.DynamicSurveyRepository;
import team.startup.expo.domain.survey.management.repository.SurveyRepository;
import team.startup.expo.domain.survey.management.service.GetSurveyService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ReadOnlyTransactionService
@RequiredArgsConstructor
//@CacheConfig(cacheNames = "Survey")
public class GetSurveyServiceImpl implements GetSurveyService {

    private final SurveyRepository surveyRepository;
    private final DynamicSurveyRepository dynamicSurveyRepository;
    private final ExpoRepository expoRepository;
    private final DynamicSurveyJsonRepository dynamicSurveyJsonRepository;

//    @Cacheable(key = "#expoId + '_' + #participationType", cacheManager = "cacheManager")
    public SurveyResponseDto execute(String expoId, ParticipationType participationType) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        Survey survey = surveyRepository.findByExpoAndParticipationType(expo, participationType)
                .orElseThrow(NotFoundSurveyException::new);

        List<DynamicSurvey> dynamicSurveyList = dynamicSurveyRepository.findBySurveyId(survey.getId());

        List<Long> ids = dynamicSurveyList.stream()
                .map(DynamicSurvey::getId)
                .toList();
        Map<Long, DynamicSurveyJsonDoc> docMap = dynamicSurveyJsonRepository.findByRecordIdIn(ids).stream()
                .collect(Collectors.toMap(DynamicSurveyJsonDoc::getRecordId, d -> d));

        List<SurveyResponseDto.DynamicSurveyResponseDto> dynamicSurveyResponseDto = dynamicSurveyList.stream()
                .map(dynamicSurvey -> {
                    DynamicSurveyJsonDoc doc = docMap.get(dynamicSurvey.getId());
                    return SurveyResponseDto.DynamicSurveyResponseDto.builder()
                        .title(dynamicSurvey.getTitle())
                        .jsonData(doc != null ? doc.getJsonData() : null)
                        .formType(dynamicSurvey.getFormType())
                        .requiredStatus(dynamicSurvey.getRequiredStatus())
                        .otherJson(doc != null ? doc.getOtherJson() : null)
                        .build();
                }).toList();

        return SurveyResponseDto.builder()
                .informationText(survey.getInformationText())
                .participationType(participationType)
                .title(survey.getTitle())
                .dynamicSurveyResponseDto(dynamicSurveyResponseDto)
                .build();
    }
}
