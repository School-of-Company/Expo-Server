package team.startup.expo.domain.attendance.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import team.startup.expo.domain.form.entity.ParticipationType;

@RedisHash(value = "leave_manager", timeToLive = 30L)
@Builder
@Getter
public class LeaveManager {
    @Id
    private String phoneNumber;
    @Indexed
    private String expoId;
    private ParticipationType participationType;


}



//@RedisHash(value = "phone_authentication", timeToLive = 60L * 3)
//@Builder
//@Getter
//public class SmsAuthEntity {
//    @Id
//    private String phone;
//    @Indexed
//    private String randomValue;
//    private Boolean authentication;
//    private Integer attemptCount;