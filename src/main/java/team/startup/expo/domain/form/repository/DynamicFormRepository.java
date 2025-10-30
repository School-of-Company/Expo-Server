package team.startup.expo.domain.form.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.form.entity.DynamicForm;

import java.util.List;

public interface DynamicFormRepository extends JpaRepository<DynamicForm, Long> {
    void deleteByFormId(Long formId);
    List<DynamicForm> findByFormId(Long formId);
}
