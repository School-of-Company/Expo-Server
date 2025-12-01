package team.startup.expo.domain.form.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.form.exception.NotFoundFormException;
import team.startup.expo.domain.form.presentation.dto.response.GetFormResponseDto;
import team.startup.expo.domain.form.repository.DynamicFormRepository;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.form.repository.projection.DynamicFormDto;
import team.startup.expo.domain.form.service.GetFormService;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
@CacheConfig(cacheNames = "Form")
public class GetFormServiceImpl implements GetFormService {

    private final FormRepository formRepository;
    private final ExpoRepository expoRepository;
    private final DynamicFormRepository dynamicFormRepository;

    @Cacheable(key = "#expoId + '_' + #participationType", cacheManager = "cacheManager")
    public GetFormResponseDto execute(String expoId, ParticipationType participationType, ApplicationType applicationType) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        Form form = formRepository.findByExpoAndParticipationTypeAndApplicationType(expo, participationType, applicationType)
                .orElseThrow(NotFoundFormException::new);

        List<DynamicFormDto> dynamicFormDtos = dynamicFormRepository.findAllByFormIdWithJson(form.getId());

        List<GetFormResponseDto.DynamicFormResponseDto> dynamicFormResponseDtos = dynamicFormDtos.stream()
                .map(dynamicFormDto -> GetFormResponseDto.DynamicFormResponseDto.builder()
                        .title(dynamicFormDto.title())
                        .formType(dynamicFormDto.formType())
                        .jsonData(dynamicFormDto.jsonData())
                        .requiredStatus(dynamicFormDto.requiredStatus())
                        .otherJson(dynamicFormDto.otherJson())
                        .build())
                .toList();

        return GetFormResponseDto.builder()
                .title(form.getTitle())
                .informationText(form.getInformationText())
                .participantType(form.getParticipationType())
                .applicationType(form.getApplicationType())
                .dynamicForm(dynamicFormResponseDtos)
                .startDate(form.getStartDate())
                .endDate(form.getEndDate())
                .build();
    }
}
