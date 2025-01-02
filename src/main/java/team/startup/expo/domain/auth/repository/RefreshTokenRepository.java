package team.startup.expo.domain.auth.repository;

import org.springframework.data.repository.CrudRepository;
import team.startup.expo.global.util.count.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String refreshToken);
}
