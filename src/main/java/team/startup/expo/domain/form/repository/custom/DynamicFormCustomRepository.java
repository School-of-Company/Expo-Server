package team.startup.expo.domain.form.repository.custom;

import java.util.List;

public interface DynamicFormCustomRepository {
    List<Long> findIdsByFormId(Long formId);
    long deleteByFormId(Long formId);
}
