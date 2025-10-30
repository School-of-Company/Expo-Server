package team.startup.expo.domain.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "participant_answer_json")
@CompoundIndex(def = "{'ownerType': 1, 'ownerId': 1}", unique = true)
public class DynamicJsonData {
    @Id
    private String id;

    @Indexed
    private OwnerType ownerType;

    @Indexed
    private Long ownerId;

    private Map<String, Object> answers;
}
