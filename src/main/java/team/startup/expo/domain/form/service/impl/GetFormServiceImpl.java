package team.startup.expo.domain.form.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.form.entity.DynamicForm;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.form.exception.NotFoundFormException;
import team.startup.expo.domain.form.presentation.dto.GetFormResponseDto;
import team.startup.expo.domain.form.repository.DynamicFormRepository;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.form.service.GetFormService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetFormServiceImpl implements GetFormService {

    private final FormRepository formRepository;
    private final DynamicFormRepository dynamicFormRepository;

    public GetFormResponseDto execute(Long formId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(NotFoundFormException::new);

        List<DynamicForm> dynamicFormList = dynamicFormRepository.findByForm(form);

        List<GetFormResponseDto.DynamicFormRequestDto> dynamicFormRequestDtoList = dynamicFormList.stream()
                .map(dynamicForm -> GetFormResponseDto.DynamicFormRequestDto.builder()
                        .title(dynamicForm.getTitle())
                        .jsonData(dynamicForm.getJsonData())
                        .formType(dynamicForm.getFormType())
                        .build()
                ).toList();

        return GetFormResponseDto.builder()
                .informationImage(form.getInformationImage())
                .participantType(form.getParticipationType())
                .dynamicForm(dynamicFormRequestDtoList)
                .build();
    }
}
