package team.startup.expo.domain.sms.repository;

import org.springframework.data.repository.CrudRepository;
import team.startup.expo.domain.sms.entity.SmsAuthEntity;

public interface SmsAuthRepository extends CrudRepository<SmsAuthEntity, String> {
}
