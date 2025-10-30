package team.startup.expo.domain.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import team.startup.expo.domain.mongo.entity.DynamicSurveyJsonDoc;

import java.util.Optional;

public interface DynamicSurveyJsonRepository extends MongoRepository<DynamicSurveyJsonDoc, String> {
    Optional<DynamicSurveyJsonDoc> findByRecordId(Long recordId);
    void deleteByRecordId(Long recordId);
}
