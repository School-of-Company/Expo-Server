package team.startup.expo.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import team.startup.expo.domain.admin.Admin;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByNickname(String nickname);
}
