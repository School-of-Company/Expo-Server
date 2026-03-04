package team.startup.expo.domain.form.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.DynamicForm;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.exception.NotFoundFormException;
import team.startup.expo.domain.form.presentation.dto.request.FormRequestDto;
import team.startup.expo.domain.form.repository.DynamicFormRepository;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.form.service.UpdateFormService;
import team.startup.expo.domain.json.entity.DynamicJson;
import team.startup.expo.domain.json.entity.DynamicJsonType;
import team.startup.expo.domain.json.repository.DynamicJsonRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
@CacheConfig(cacheNames = "Form")
public class UpdateFormServiceImpl implements UpdateFormService {

    private final ExpoRepository expoRepository;
    private final FormRepository formRepository;
    private final DynamicFormRepository dynamicFormRepository;
    private final DynamicJsonRepository dynamicJsonRepository;

    @CacheEvict(key = "#expoId + '_' + #dto.participantType + '_' + #dto.applicationType", cacheManager = "cacheManager")
    public void execute(String expoId, FormRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundFormException::new);

        Form form = formRepository.findByExpoAndParticipationTypeAndApplicationType(expo, dto.getParticipantType(), dto.getApplicationType())
                .orElseThrow(NotFoundFormException::new);

        dynamicFormRepository.deleteByFormId(form.getId());

        form.updateForm(dto);
        dto.getDynamicForm().forEach(dynamicForm -> {saveDynamicForm(dynamicForm, form);});
    }

    private void saveDynamicForm(FormRequestDto.DynamicFormRequestDto dto, Form form) {
        DynamicForm dynamicForm = dynamicFormRepository.save(DynamicForm.builder()
                .form(form)
                .title(dto.getTitle())
                .formType(dto.getFormType())
                .requiredStatus(dto.getRequiredStatus())
                .build());

        dynamicJsonRepository.save(DynamicJson.builder()
                .dynamicJsonType(DynamicJsonType.FORM)
                .recordId(dynamicForm.getId())
                .jsonData(dto.getJsonData())
                .otherJson(dto.getOtherJson())
                .build());
    }
}
