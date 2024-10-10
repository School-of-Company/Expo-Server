package team.startup.expo.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import team.startup.expo.domain.user.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
