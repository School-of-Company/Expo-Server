package team.startup.expo.domain.expo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.expo.domain.expo.Expo;

public interface ExpoRepository extends JpaRepository<Expo, Long> {
}
