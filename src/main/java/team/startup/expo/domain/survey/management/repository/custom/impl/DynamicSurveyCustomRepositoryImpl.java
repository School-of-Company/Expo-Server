package team.startup.expo.domain.survey.management.repository.custom.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.startup.expo.domain.json.entity.DynamicJsonType;
import team.startup.expo.domain.survey.management.repository.custom.DynamicSurveyCustomRepository;
import team.startup.expo.domain.survey.management.repository.projection.DynamicSurveyDto;

import java.util.List;

import static team.startup.expo.domain.json.entity.QDynamicJson.dynamicJson;
import static team.startup.expo.domain.survey.management.entity.QDynamicSurvey.dynamicSurvey;

@Repository
@RequiredArgsConstructor
public class DynamicSurveyCustomRepositoryImpl implements DynamicSurveyCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public long deleteBySurveyId(Long surveyId) {
        return queryFactory
                .delete(dynamicSurvey)
                .where(dynamicSurvey.survey.id.eq(surveyId))
                .execute();
    }

    @Override
    public List<DynamicSurveyDto> findAllBySurveyIdWithJson(Long surveyId) {
        return queryFactory
                .select(Projections.constructor(
                        DynamicSurveyDto.class,
                        dynamicSurvey.id,
                        dynamicSurvey.formType,
                        dynamicSurvey.title,
                        dynamicJson.jsonData,
                        dynamicSurvey.requiredStatus,
                        dynamicJson.otherJson,
                        dynamicSurvey.survey.id
                ))
                .from(dynamicSurvey)
                .join(dynamicJson)
                .on(
                        dynamicJson.recordId.eq(dynamicSurvey.id),
                        (dynamicJson.dynamicJsonType.eq(DynamicJsonType.SURVEY))
                )
                .where(dynamicSurvey.survey.id.eq(surveyId))
                .orderBy(dynamicSurvey.id.asc())
                .fetch();
    }
}
