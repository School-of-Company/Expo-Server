package team.startup.expo.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import team.startup.expo.domain.admin.Admin;
import team.startup.expo.domain.admin.Status;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByEmail(String email);
    Boolean existsByNickname(String nickname);
    Optional<Admin> findByNickname(String nickname);
    List<Admin> findByStatus(Status status);
}
