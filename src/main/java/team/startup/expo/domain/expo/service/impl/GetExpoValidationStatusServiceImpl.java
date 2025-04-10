package team.startup.expo.domain.expo.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.presentation.dto.response.GetExpoValidationStatusResponseDto;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.expo.service.GetExpoValidationStatusService;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.survey.management.repository.SurveyRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetExpoValidationStatusServiceImpl implements GetExpoValidationStatusService {

    private final ExpoRepository expoRepository;
    private final FormRepository formRepository;
    private final SurveyRepository surveyRepository;

    public GetExpoValidationStatusResponseDto execute() {
        List<Expo> allExpo = expoRepository.findAll();

        List<GetExpoValidationStatusResponseDto.ExpoValidDto> expoValidDto = allExpo.stream().map(expo ->
                GetExpoValidationStatusResponseDto.ExpoValidDto.builder()
                        .expoId(expo.getId())
                        .standardFormCreatedStatus(formRepository.existsByExpoAndParticipationType(expo, ParticipationType.STANDARD))
                        .traineeFormCreatedStatus(formRepository.existsByExpoAndParticipationType(expo, ParticipationType.TRAINEE))
                        .StandardSurveyCreatedStatus(surveyRepository.existsByExpoAndParticipationType(expo, ParticipationType.STANDARD))
                        .traineeSurveyCreatedStatus(surveyRepository.existsByExpoAndParticipationType(expo, ParticipationType.TRAINEE))
                        .build()
        ).toList();

        return GetExpoValidationStatusResponseDto.builder()
                .expoValid(expoValidDto)
                .build();
    }
}
