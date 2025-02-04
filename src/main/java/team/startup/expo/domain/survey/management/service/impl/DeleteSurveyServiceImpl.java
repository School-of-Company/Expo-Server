package team.startup.expo.domain.survey.management.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.survey.management.entity.Survey;
import team.startup.expo.domain.survey.management.repository.DynamicSurveyRepository;
import team.startup.expo.domain.survey.management.repository.SurveyRepository;
import team.startup.expo.domain.survey.management.service.DeleteSurveyService;
import team.startup.expo.global.annotation.TransactionService;

import java.util.List;

@TransactionService
@RequiredArgsConstructor
public class DeleteSurveyServiceImpl implements DeleteSurveyService {

    private final SurveyRepository surveyRepository;
    private final DynamicSurveyRepository dynamicSurveyRepository;
    private final ExpoRepository expoRepository;

    public void execute(String expoId) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        List<Survey> surveyList = surveyRepository.findByExpo(expo);

        dynamicSurveyRepository.deleteBySurveys(surveyList);
        surveyRepository.deleteAll(surveyList);
    }
}
