package team.startup.expo.domain.form.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.form.entity.Form;

public interface FormRepository extends JpaRepository<Form, Long> {
}
