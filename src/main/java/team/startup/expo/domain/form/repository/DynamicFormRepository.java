package team.startup.expo.domain.form.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.form.entity.DynamicForm;
import team.startup.expo.domain.form.entity.Form;

import java.util.List;

public interface DynamicFormRepository extends JpaRepository<DynamicForm, Long> {
    void deleteByForm(Form form);
    List<DynamicForm> findByForm(Form form);
}
