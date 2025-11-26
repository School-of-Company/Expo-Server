package team.startup.expo.domain.json.repository.custom.impl;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.startup.expo.domain.json.entity.DynamicJsonType;
import team.startup.expo.domain.json.repository.custom.DynamicJsonCustomRepository;

import static team.startup.expo.domain.form.entity.QDynamicForm.dynamicForm;
import static team.startup.expo.domain.json.entity.QDynamicJson.dynamicJson;
import static team.startup.expo.domain.survey.management.entity.QDynamicSurvey.dynamicSurvey;

@Repository
@RequiredArgsConstructor
public class DynamicJsonCustomRepositoryImpl implements DynamicJsonCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteAllByFormId(Long formId) {
        var formIdSubQuery = queryFactory
                .select(dynamicForm.id)
                .from(dynamicForm)
                .where(dynamicForm.form.id.eq(formId));

        deleteAllByRecordIdSubQuery(formIdSubQuery, DynamicJsonType.FORM);
    }

    @Override
    public void deleteAllBySurveyId(Long surveyId) {
        var surveyIdSubQuery = queryFactory
                .select(dynamicSurvey.id)
                .from(dynamicSurvey)
                .where(dynamicSurvey.survey.id.eq(surveyId));

        deleteAllByRecordIdSubQuery(surveyIdSubQuery, DynamicJsonType.SURVEY);
    }

    private void deleteAllByRecordIdSubQuery(JPQLQuery<Long> recordIdSubQuery, DynamicJsonType dynamicJsonType) {
        queryFactory
                .delete(dynamicJson)
                .where(
                        dynamicJson.recordId.in(recordIdSubQuery),
                        dynamicJson.dynamicJsonType.eq(dynamicJsonType)
                )
                .execute();
    }
}
