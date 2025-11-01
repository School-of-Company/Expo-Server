package team.startup.expo.domain.survey.management.repository.custom.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.startup.expo.domain.survey.management.repository.custom.DynamicSurveyCustomRepository;

import java.util.List;

import static team.startup.expo.domain.survey.management.entity.QDynamicSurvey.dynamicSurvey;

@Repository
@RequiredArgsConstructor
public class DynamicSurveyCustomRepositoryImpl implements DynamicSurveyCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findIdsBySurveyId(Long surveyId) {
        return queryFactory
                .select(dynamicSurvey.id)
                .from(dynamicSurvey)
                .where(dynamicSurvey.surveyId.eq(surveyId))
                .fetch();
    }

    @Override
    public long deleteBySurveyId(Long surveyId) {
        return queryFactory
                .delete(dynamicSurvey)
                .where(dynamicSurvey.surveyId.eq(surveyId))
                .execute();
    }
}
