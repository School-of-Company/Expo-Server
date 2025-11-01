package team.startup.expo.domain.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import team.startup.expo.domain.mongo.entity.DynamicJsonData;
import team.startup.expo.domain.mongo.entity.OwnerType;

import java.util.Optional;

public interface DynamicJsonDataRepository extends MongoRepository<DynamicJsonData, String> {
    Optional<DynamicJsonData> findByOwnerTypeAndOwnerId(OwnerType ownerType, Long ownerId);
}
