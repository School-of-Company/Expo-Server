package team.startup.expo.domain.form.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.form.entity.DynamicForm;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.exception.NotFoundFormException;
import team.startup.expo.domain.form.presentation.dto.request.FormRequestDto;
import team.startup.expo.domain.form.repository.DynamicFormRepository;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.form.service.UpdateFormService;
import team.startup.expo.global.annotation.TransactionService;

import java.util.List;

@TransactionService
@RequiredArgsConstructor
public class UpdateFormServiceImpl implements UpdateFormService {

    private final FormRepository formRepository;
    private final DynamicFormRepository dynamicFormRepository;

    public void execute(Long formId, FormRequestDto dto) {
        Form form = formRepository.findById(formId)
                .orElseThrow(NotFoundFormException::new);

        dynamicFormRepository.deleteByForm(form);

        form.updateForm(dto);
        dto.getDynamicForm().forEach(dynamicForm -> {saveDynamicForm(dynamicForm, form);});
    }

    private void saveDynamicForm(FormRequestDto.DynamicFormRequestDto dto, Form form) {
        DynamicForm updateDynamicForm = DynamicForm.builder()
                .title(dto.getTitle())
                .formType(dto.getFormType())
                .jsonData(dto.getJsonData())
                .requiredStatus(dto.getRequiredStatus())
                .otherJson(dto.getOtherJson())
                .form(form)
                .build();

        dynamicFormRepository.save(updateDynamicForm);
    }
}
