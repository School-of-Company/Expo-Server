package team.startup.expo.domain.json.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor
@Entity
@Builder
@Table(
        name = "tb_dynamic_json",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"dynamic_json_type", "record_id"})}
)
@AllArgsConstructor
public class DynamicJson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "dynamic_json_type", nullable = false, length = 50)
    private DynamicJsonType dynamicJsonType;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "json_data", nullable = false, columnDefinition = "jsonb")
    private String jsonData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "other_json", columnDefinition = "jsonb")
    private String otherJson;

}
