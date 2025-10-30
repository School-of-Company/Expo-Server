package team.startup.expo.domain.form.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.DynamicForm;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.form.exception.NotFoundFormException;
import team.startup.expo.domain.form.presentation.dto.response.GetFormResponseDto;
import team.startup.expo.domain.form.repository.DynamicFormRepository;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.form.service.GetFormService;
import team.startup.expo.domain.mongo.entity.DynamicFormJsonDoc;
import team.startup.expo.domain.mongo.repository.DynamicFormJsonRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
@CacheConfig(cacheNames = "Form")
public class GetFormServiceImpl implements GetFormService {

    private final FormRepository formRepository;
    private final ExpoRepository expoRepository;
    private final DynamicFormRepository dynamicFormRepository;
    private final DynamicFormJsonRepository dynamicFormJsonRepository;

    @Cacheable(key = "#expoId + '_' + #participationType", cacheManager = "cacheManager")
    public GetFormResponseDto execute(String expoId, ParticipationType participationType) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        Form form = formRepository.findByExpoAndParticipationType(expo, participationType)
                .orElseThrow(NotFoundFormException::new);

        List<DynamicForm> dynamicFormList = dynamicFormRepository.findByFormId(form.getId());

        List<GetFormResponseDto.DynamicFormRequestDto> dynamicFormRequestDtoList = dynamicFormList.stream()
                .map(dynamicForm -> {
                    DynamicFormJsonDoc doc = dynamicFormJsonRepository.findByRecordId(dynamicForm.getId()).orElse(null);
                    return GetFormResponseDto.DynamicFormRequestDto.builder()
                            .title(dynamicForm.getTitle())
                            .jsonData(doc != null ? doc.getJsonData() : null)
                            .formType(dynamicForm.getFormType())
                            .requiredStatus(dynamicForm.getRequiredStatus())
                            .otherJson(doc != null ? doc.getOtherJson() : null)
                            .build();
                }).toList();

        return GetFormResponseDto.builder()
                .informationText(form.getInformationText())
                .participantType(form.getParticipationType())
                .dynamicForm(dynamicFormRequestDtoList)
                .build();
    }
}
