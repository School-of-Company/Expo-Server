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
import team.startup.expo.domain.json.entity.DynamicJson;
import team.startup.expo.domain.json.entity.DynamicJsonType;
import team.startup.expo.domain.json.repository.DynamicJsonRepository;
import team.startup.expo.global.annotation.TransactionService;

import java.util.ArrayList;
import java.util.List;

@TransactionService
@RequiredArgsConstructor
public class CreateFormServiceImpl implements CreateFormService {

    private final FormRepository formRepository;
    private final DynamicFormRepository dynamicFormRepository;
    private final ExpoRepository expoRepository;
    private final DynamicJsonRepository dynamicJsonRepository;

    public void execute(String expoId, FormRequestDto formRequestDto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (formRepository.existsByExpoAndParticipationTypeAndApplicationType(expo, formRequestDto.getParticipantType(), formRequestDto.getApplicationType()))
            throw new AlreadyExistFormException();

        Form form = saveForm(formRequestDto, expo);

        saveDynamicForms(formRequestDto.getDynamicForm(), form);
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

    private void saveDynamicForms(List<FormRequestDto.DynamicFormRequestDto> dtos, Form form) {
        List<DynamicForm> dynamicForms = dtos.stream()
                .map(dto -> DynamicForm.builder()
                        .form(form)
                        .title(dto.getTitle())
                        .formType(dto.getFormType())
                        .requiredStatus(dto.getRequiredStatus())
                        .build())
                .toList();

        List<DynamicForm> savedForms = dynamicFormRepository.saveAll(dynamicForms);

        List<DynamicJson> dynamicJsons = new ArrayList<>();
        for (int i = 0; i < savedForms.size(); i++) {
            FormRequestDto.DynamicFormRequestDto dto = dtos.get(i);
            dynamicJsons.add(DynamicJson.builder()
                    .dynamicJsonType(DynamicJsonType.FORM)
                    .recordId(savedForms.get(i).getId())
                    .jsonData(dto.getJsonData())
                    .otherJson(dto.getOtherJson())
                    .build());
        }

        dynamicJsonRepository.saveAll(dynamicJsons);
    }
}
