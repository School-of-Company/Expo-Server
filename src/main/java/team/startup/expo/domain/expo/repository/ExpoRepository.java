package team.startup.expo.domain.expo.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import team.startup.expo.domain.expo.entity.Expo;

import java.util.Optional;

public interface ExpoRepository extends JpaRepository<Expo, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Expo e WHERE e.id = :id")
    Optional<Expo> findByIdWithLock(String id);
}
