package team.startup.expo.domain.attendance.repository;

import org.springframework.data.repository.CrudRepository;
import team.startup.expo.domain.attendance.entity.LeaveManager;

public interface LeaveManagerRepository extends CrudRepository<LeaveManager, String> {
}
