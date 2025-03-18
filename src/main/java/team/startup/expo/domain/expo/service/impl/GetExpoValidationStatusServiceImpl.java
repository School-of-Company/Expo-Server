package team.startup.expo.domain.expo.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.presentation.dto.response.GetExpoValidationStatusResponseDto;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.expo.service.GetExpoValidationStatusService;
import team.startup.expo.domain.form.repository.DynamicFormRepository;
import team.startup.expo.domain.survey.management.repository.SurveyRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetExpoValidationStatusServiceImpl implements GetExpoValidationStatusService {

    private final ExpoRepository expoRepository;
    private final DynamicFormRepository dynamicFormRepository;
    private final SurveyRepository surveyRepository;

    public GetExpoValidationStatusResponseDto execute(String expoId) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        boolean dynamicFormExists = dynamicFormRepository.existsByExpo(expo);
        boolean surveyExists = surveyRepository.existsByExpo(expo);

        return GetExpoValidationStatusResponseDto.builder()
                .dynamicFormCreatedStatus(dynamicFormExists)
                .surveyCreatedStatus(surveyExists)
                .build();
    }
}
