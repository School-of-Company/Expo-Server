package team.startup.expo.domain.survey.management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.survey.management.entity.DynamicSurvey;
import team.startup.expo.domain.survey.management.entity.Survey;
import team.startup.expo.domain.survey.management.exception.NotFoundSurveyException;
import team.startup.expo.domain.survey.management.repository.DynamicSurveyRepository;
import team.startup.expo.domain.survey.management.repository.SurveyRepository;
import team.startup.expo.domain.survey.management.service.DeleteSurveyService;
import team.startup.expo.domain.mongo.repository.DynamicSurveyJsonRepository;
import team.startup.expo.global.annotation.TransactionService;

import java.util.List;

@TransactionService
@RequiredArgsConstructor
@CacheConfig(cacheNames = "Survey")
public class DeleteSurveyServiceImpl implements DeleteSurveyService {

    private final SurveyRepository surveyRepository;
    private final DynamicSurveyRepository dynamicSurveyRepository;
    private final ExpoRepository expoRepository;
    private final DynamicSurveyJsonRepository dynamicSurveyJsonRepository;

    @CacheEvict(key = "#expoId + '_' + #participationType", cacheManager = "cacheManager")
    public void execute(String expoId, ParticipationType participationType) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        Survey survey = surveyRepository.findByExpoAndParticipationType(expo, participationType)
                .orElseThrow(NotFoundSurveyException::new);

        List<DynamicSurvey> dynamicSurveys = dynamicSurveyRepository.findBySurveyId(survey.getId());
        for (DynamicSurvey ds : dynamicSurveys) {
            dynamicSurveyJsonRepository.deleteByRecordId(ds.getId());
        }
        dynamicSurveyRepository.deleteAll(dynamicSurveys);
        surveyRepository.delete(survey);
    }
}
