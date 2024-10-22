package team.startup.expo.domain.auth.repository;

import org.springframework.data.repository.CrudRepository;
import team.startup.expo.global.util.count.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
