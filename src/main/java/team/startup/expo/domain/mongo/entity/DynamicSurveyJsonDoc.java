package team.startup.expo.domain.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "dynamic_survey_json")
public class DynamicSurveyJsonDoc {

    @Id
    private String id;

    @Indexed(unique = true)
    private Long recordId;

    private String jsonData;

    private String otherJson;
}
