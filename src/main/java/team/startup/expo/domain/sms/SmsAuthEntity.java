package team.startup.expo.domain.sms;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "phone_authentication", timeToLive = 60L * 3)
@Builder
@Getter
public class SmsAuthEntity {
    @Id
    private String phone;
    @Indexed
    private String randomValue;
    private Boolean authentication;
    private Integer attemptCount;
}
