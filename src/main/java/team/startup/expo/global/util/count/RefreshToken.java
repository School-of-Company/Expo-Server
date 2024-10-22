package team.startup.expo.global.util.count;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import team.startup.expo.global.security.jwt.JwtProvider;

@RedisHash(value = "expo_refreshToken", timeToLive = JwtProvider.REFRESH_TOKEN_TIME)
@Builder
@Getter
public class RefreshToken {
    @Id
    @Indexed
    private String email;
    @Indexed
    private String token;
}