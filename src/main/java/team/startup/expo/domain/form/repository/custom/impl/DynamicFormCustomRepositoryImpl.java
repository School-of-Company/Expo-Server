package team.startup.expo.domain.form.repository.custom.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.startup.expo.domain.form.repository.custom.DynamicFormCustomRepository;

import java.util.List;

import static team.startup.expo.domain.form.entity.QDynamicForm.dynamicForm;

@Repository
@RequiredArgsConstructor
public class DynamicFormCustomRepositoryImpl implements DynamicFormCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findIdsByFormId(Long formId) {
        return queryFactory
                .select(dynamicForm.id)
                .from(dynamicForm)
                .where(dynamicForm.formId.eq(formId))
                .fetch();
    }

    @Override
    public long deleteByFormId(Long formId) {
        return queryFactory
                .delete(dynamicForm)
                .where(dynamicForm.formId.eq(formId))
                .execute();
    }
}