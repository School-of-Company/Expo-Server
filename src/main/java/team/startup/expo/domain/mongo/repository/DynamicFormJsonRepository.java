package team.startup.expo.domain.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import team.startup.expo.domain.mongo.entity.DynamicFormJsonDoc;

import java.util.Optional;
import java.util.Collection;

public interface DynamicFormJsonRepository extends MongoRepository<DynamicFormJsonDoc, String> {
    Optional<DynamicFormJsonDoc> findByRecordId(Long recordId);
    void deleteByRecordIdIn(Collection<Long> recordIds);

}
