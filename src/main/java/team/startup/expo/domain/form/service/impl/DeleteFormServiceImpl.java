package team.startup.expo.domain.form.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.exception.NotFoundFormException;
import team.startup.expo.domain.form.repository.DynamicFormRepository;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.form.service.DeleteFormService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class DeleteFormServiceImpl implements DeleteFormService {

    private final FormRepository formRepository;
    private final DynamicFormRepository dynamicFormRepository;

    public void execute(Long formId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(NotFoundFormException::new);

        dynamicFormRepository.deleteByForm(form);
        formRepository.delete(form);
    }
}
