package team.startup.expo.domain.form.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.DynamicForm;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.exception.AlreadyExistFormException;
import team.startup.expo.domain.form.presentation.dto.request.CreateFormRequestDto;
import team.startup.expo.domain.form.repository.DynamicFormRepository;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.form.service.CreateFormService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class CreateFormServiceImpl implements CreateFormService {

    private final FormRepository formRepository;
    private final DynamicFormRepository dynamicFormRepository;
    private final ExpoRepository expoRepository;

    public void execute(String expoId, CreateFormRequestDto createFormRequestDto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (formRepository.existsByExpoAndParticipationType(expo, createFormRequestDto.getParticipantType()))
            throw new AlreadyExistFormException();

        Form form = saveForm(createFormRequestDto, expo);

        createFormRequestDto.getDynamicForm().forEach(dynamicForm -> {saveDynamicForm(dynamicForm, form);});
    }

    private Form saveForm(CreateFormRequestDto createFormRequestDto, Expo expo) {
        Form form = Form.builder()
                .informationImage(createFormRequestDto.getInformationImage())
                .participationType(createFormRequestDto.getParticipantType())
                .expo(expo)
                .build();

        return formRepository.save(form);
    }

    private void saveDynamicForm(CreateFormRequestDto.CreateDynamicFormRequestDto dynamicFormRequestDto, Form form) {
        DynamicForm dynamicForm = DynamicForm.builder()
                .title(dynamicFormRequestDto.getTitle())
                .jsonData(dynamicFormRequestDto.getJsonData())
                .formType(dynamicFormRequestDto.getFormType())
                .form(form)
                .build();

        dynamicFormRepository.save(dynamicForm);
    }
}
