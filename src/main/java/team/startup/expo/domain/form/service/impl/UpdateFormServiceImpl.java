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

        List<DynamicForm> dynamicFormList = dynamicFormRepository.findByForm(form);

        form.updateForm(dto);
        dto.getDynamicForm().forEach(dynamicFormDto -> dynamicFormList.forEach(dynamicForm -> updateDynamicForm(dynamicFormDto, dynamicForm)));
    }

    private void updateDynamicForm(FormRequestDto.DynamicFormRequestDto dto, DynamicForm dynamicForm) {
        DynamicForm updateDynamicForm = DynamicForm.builder()
                .id(dynamicForm.getId())
                .title(dto.getTitle())
                .formType(dto.getFormType())
                .jsonData(dto.getJsonData())
                .form(dynamicForm.getForm())
                .build();

        dynamicFormRepository.save(updateDynamicForm);
    }
}
