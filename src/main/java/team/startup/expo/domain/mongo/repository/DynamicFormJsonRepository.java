package team.startup.expo.domain.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import team.startup.expo.domain.mongo.entity.DynamicFormJsonDoc;

import java.util.Optional;
import java.util.Collection;
import java.util.List;

public interface DynamicFormJsonRepository extends MongoRepository<DynamicFormJsonDoc, String> {
    Optional<DynamicFormJsonDoc> findByRecordId(Long recordId);
    List<DynamicFormJsonDoc> findByRecordIdIn(Collection<Long> recordIds);
    void deleteByRecordIdIn(Collection<Long> recordIds);

}
