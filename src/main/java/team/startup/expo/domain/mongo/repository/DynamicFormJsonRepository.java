package team.startup.expo.domain.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import team.startup.expo.domain.mongo.entity.DynamicFormJsonDoc;

import java.util.Optional;

public interface DynamicFormJsonRepository extends MongoRepository<DynamicFormJsonDoc, String> {
    Optional<DynamicFormJsonDoc> findByRecordId(Long recordId);
    void deleteByRecordId(Long recordId);

}
