package team.startup.expo.domain.form.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.form.entity.DynamicForm;
import team.startup.expo.domain.form.repository.custom.DynamicFormCustomRepository;

public interface DynamicFormRepository extends JpaRepository<DynamicForm, Long>, DynamicFormCustomRepository {
}
