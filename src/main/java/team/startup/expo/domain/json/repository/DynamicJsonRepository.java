package team.startup.expo.domain.json.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.json.entity.DynamicJson;
import team.startup.expo.domain.json.repository.custom.DynamicJsonCustomRepository;

public interface DynamicJsonRepository extends JpaRepository<DynamicJson, Long>, DynamicJsonCustomRepository {
}
