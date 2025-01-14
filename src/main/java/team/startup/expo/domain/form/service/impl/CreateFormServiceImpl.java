package team.startup.expo.domain.form.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.DynamicForm;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.exception.AlreadyExistFormException;
import team.startup.expo.domain.form.presentation.dto.request.FormRequestDto;
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

    public void execute(String expoId, FormRequestDto formRequestDto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (formRepository.existsByExpoAndParticipationType(expo, formRequestDto.getParticipantType()))
            throw new AlreadyExistFormException();

        Form form = saveForm(formRequestDto, expo);

        formRequestDto.getDynamicForm().forEach(dynamicForm -> {saveDynamicForm(dynamicForm, form);});
    }

    private Form saveForm(FormRequestDto formRequestDto, Expo expo) {
        Form form = Form.builder()
                .informationImage(formRequestDto.getInformationImage())
                .participationType(formRequestDto.getParticipantType())
                .expo(expo)
                .build();

        return formRepository.save(form);
    }

    private void saveDynamicForm(FormRequestDto.CreateDynamicFormRequestDto dynamicFormRequestDto, Form form) {
        DynamicForm dynamicForm = DynamicForm.builder()
                .title(dynamicFormRequestDto.getTitle())
                .jsonData(dynamicFormRequestDto.getJsonData())
                .formType(dynamicFormRequestDto.getFormType())
                .form(form)
                .build();

        dynamicFormRepository.save(dynamicForm);
    }
}
