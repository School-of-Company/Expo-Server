package team.startup.expo.domain.form.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.presentation.dto.request.CreateFormRequestDto;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.form.service.CreateFormService;
import team.startup.expo.global.annotation.TransactionService;

import java.util.List;

@TransactionService
@RequiredArgsConstructor
public class CreateFormServiceImpl implements CreateFormService {

    private final FormRepository formRepository;
    private final ExpoRepository expoRepository;

    public void execute(String expoId, List<CreateFormRequestDto> dtoList) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        dtoList.forEach(dto -> saveForm(dto, expo));
    }

    private void saveForm(CreateFormRequestDto dto, Expo expo) {
        Form form = Form.builder()
                .json(dto.getJson())
                .formType(dto.getFormType())
                .expo(expo)
                .build();

        formRepository.save(form);
    }
}
