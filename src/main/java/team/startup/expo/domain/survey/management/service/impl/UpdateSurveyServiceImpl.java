package team.startup.expo.domain.survey.management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.json.entity.DynamicJson;
import team.startup.expo.domain.json.entity.DynamicJsonType;
import team.startup.expo.domain.json.repository.DynamicJsonRepository;
import team.startup.expo.domain.survey.management.entity.DynamicSurvey;
import team.startup.expo.domain.survey.management.entity.Survey;
import team.startup.expo.domain.survey.management.exception.NotFoundSurveyException;
import team.startup.expo.domain.survey.management.presentation.dto.request.SurveyRequestDto;
import team.startup.expo.domain.survey.management.repository.DynamicSurveyRepository;
import team.startup.expo.domain.survey.management.repository.SurveyRepository;
import team.startup.expo.domain.survey.management.service.UpdateSurveyService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
@CacheConfig(cacheNames = "Survey")
public class UpdateSurveyServiceImpl implements UpdateSurveyService {

    private final SurveyRepository surveyRepository;
    private final DynamicSurveyRepository dynamicSurveyRepository;
    private final ExpoRepository expoRepository;
    private final DynamicJsonRepository dynamicJsonRepository;

    @CacheEvict(key = "#expoId + '_' + #dto.participationType", cacheManager = "cacheManager")
    public void execute(String expoId, SurveyRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        Survey survey = surveyRepository.findByExpoAndParticipationType(expo, dto.getParticipationType())
                .orElseThrow(NotFoundSurveyException::new);

        survey.update(dto.getInformationText());
        dto.getDynamicSurveyRequestDto().forEach(dynamicSurveyRequestDto -> saveDynamicSurvey(dynamicSurveyRequestDto, survey));
    }

    private void saveDynamicSurvey(SurveyRequestDto.DynamicSurveyRequestDto dto, Survey survey) {
        DynamicSurvey dynamicSurvey = dynamicSurveyRepository.save(DynamicSurvey.builder()
                .survey(survey)
                .title(dto.getTitle())
                .formType(dto.getFormType())
                .requiredStatus(dto.getRequiredStatus())
                .build());

        dynamicJsonRepository.save(DynamicJson.builder()
                .dynamicJsonType(DynamicJsonType.SURVEY)
                .recordId(dynamicSurvey.getId())
                .jsonData(dto.getJsonData())
                .otherJson(dto.getOtherJson())
                .build());
    }
}
