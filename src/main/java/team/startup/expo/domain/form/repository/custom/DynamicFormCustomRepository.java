package team.startup.expo.domain.form.repository.custom;

import team.startup.expo.domain.form.repository.projection.DynamicFormDto;

import java.util.List;

public interface DynamicFormCustomRepository {
    long deleteByFormId(Long formId);
    List<DynamicFormDto> findAllByFormIdWithJson(Long formId);
}
