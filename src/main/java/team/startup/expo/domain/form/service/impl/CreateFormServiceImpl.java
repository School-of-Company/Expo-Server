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
import team.startup.expo.domain.mongo.entity.DynamicFormJsonDoc;
import team.startup.expo.domain.mongo.repository.DynamicFormJsonRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class CreateFormServiceImpl implements CreateFormService {

    private final FormRepository formRepository;
    private final DynamicFormRepository dynamicFormRepository;
    private final ExpoRepository expoRepository;
    private final DynamicFormJsonRepository dynamicFormJsonRepository;

    public void execute(String expoId, FormRequestDto formRequestDto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (formRepository.existsByExpoAndParticipationTypeAndApplicationType(expo, formRequestDto.getParticipantType(), formRequestDto.getApplicationType()))
            throw new AlreadyExistFormException();

        Form form = saveForm(formRequestDto, expo);

        formRequestDto.getDynamicForm().forEach(df -> saveDynamicForm(df, form));
    }

    private Form saveForm(FormRequestDto formRequestDto, Expo expo) {
        Form form = Form.builder()
                .title(formRequestDto.getTitle())
                .informationText(formRequestDto.getInformationText())
                .participationType(formRequestDto.getParticipantType())
                .applicationType(formRequestDto.getApplicationType())
                .expo(expo)
                .startDate(formRequestDto.getStartDate())
                .endDate(formRequestDto.getEndDate())
                .build();

        return formRepository.save(form);
    }

    private void saveDynamicForm(FormRequestDto.DynamicFormRequestDto dynamicFormRequestDto, Form form) {
        DynamicForm dynamicForm = DynamicForm.builder()
                .formId(form.getId())
                .title(dynamicFormRequestDto.getTitle())
                .formType(dynamicFormRequestDto.getFormType())
                .requiredStatus(dynamicFormRequestDto.getRequiredStatus())
                .otherJson(dynamicFormRequestDto.getOtherJson())
                .dynamicFormType(dynamicFormRequestDto.getDynamicFormType())
                .form(form)
                .build();
        dynamicForm = dynamicFormRepository.save(dynamicForm);

        DynamicFormJsonDoc doc = new DynamicFormJsonDoc(
                null,
                dynamicForm.getId(),
                dynamicFormRequestDto.getJsonData(),
                dynamicFormRequestDto.getOtherJson()
        );
        dynamicFormJsonRepository.save(doc);
    }
}
