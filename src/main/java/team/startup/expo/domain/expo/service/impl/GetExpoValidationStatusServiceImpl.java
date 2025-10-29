package team.startup.expo.domain.expo.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.presentation.dto.response.GetExpoValidationStatusResponseDto;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.expo.service.GetExpoValidationStatusService;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.form.entity.RegistrationType;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.survey.management.entity.Survey;
import team.startup.expo.domain.survey.management.repository.SurveyRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetExpoValidationStatusServiceImpl implements GetExpoValidationStatusService {

    private final ExpoRepository expoRepository;
    private final FormRepository formRepository;
    private final SurveyRepository surveyRepository;

    public GetExpoValidationStatusResponseDto execute() {
        List<Expo> allExpo = expoRepository.findAll();

        List<Form> forms = formRepository.findAllByExpoIn(allExpo);
        Map<String, List<Form>> formMap = forms.stream()
                .collect(Collectors.groupingBy(
                        form -> form.getExpo().getId(),
                        Collectors.toList()
                ));

        List<Survey> surveys = surveyRepository.findAllByExpoIn(allExpo);
        Map<String, List<Survey>> surveyMap = surveys.stream()
                .collect(Collectors.groupingBy(
                        survey -> survey.getExpo().getId(),
                        Collectors.toList()
                ));

//        List<GetExpoValidationStatusResponseDto.ExpoValidDto> expoValidDto = allExpo.stream().map(expo ->
//                GetExpoValidationStatusResponseDto.ExpoValidDto.builder()
//                        .expoId(expo.getId())
//                        .preStandardFormCreatedStatus(formRepository.existsByExpoAndParticipationType(expo, ParticipationType.STANDARD))
//                        .siteStandardFormCreatedStatus()
//                        .traineeFormCreatedStatus(formRepository.existsByExpoAndParticipationType(expo, ParticipationType.TRAINEE))
//                        .StandardSurveyCreatedStatus(surveyRepository.existsByExpoAndParticipationType(expo, ParticipationType.STANDARD))
//                        .traineeSurveyCreatedStatus(surveyRepository.existsByExpoAndParticipationType(expo, ParticipationType.TRAINEE))
//                        .build()
//        ).toList();

        List<GetExpoValidationStatusResponseDto.ExpoValidDto> expoValidDto = allExpo.stream().map(expo -> {
            List<Form> formList = formMap.getOrDefault(expo.getId(), Collections.emptyList());
            List<Survey> surveyList = surveyMap.getOrDefault(expo.getId(), Collections.emptyList());

            return GetExpoValidationStatusResponseDto.ExpoValidDto.builder()
                    .expoId(expo.getId())
                    .preStandardFormCreatedStatus(formList.stream().anyMatch(f -> f.getParticipationType() == ParticipationType.STANDARD && f.getRegistrationType() == RegistrationType.PRE))
                    .siteStandardFormCreatedStatus(formList.stream().anyMatch(f -> f.getParticipationType() == ParticipationType.STANDARD && f.getRegistrationType() == RegistrationType.SITE))
                    .traineeFormCreatedStatus(formList.stream().anyMatch(f -> f.getParticipationType() == ParticipationType.TRAINEE))
                    .StandardSurveyCreatedStatus(surveyList.stream().anyMatch(s -> s.getParticipationType() == ParticipationType.STANDARD))
                    .traineeSurveyCreatedStatus(surveyList.stream().anyMatch(s -> s.getParticipationType() == ParticipationType.TRAINEE))
                    .build();
        }).toList();

        return GetExpoValidationStatusResponseDto.builder()
                .expoValid(expoValidDto)
                .build();
    }
}
