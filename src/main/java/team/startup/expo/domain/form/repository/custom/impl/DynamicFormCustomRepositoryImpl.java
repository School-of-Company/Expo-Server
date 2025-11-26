package team.startup.expo.domain.form.repository.custom.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.startup.expo.domain.form.repository.custom.DynamicFormCustomRepository;
import team.startup.expo.domain.form.repository.projection.DynamicFormDto;
import team.startup.expo.domain.json.entity.DynamicJsonType;

import java.util.List;

import static team.startup.expo.domain.form.entity.QDynamicForm.dynamicForm;
import static team.startup.expo.domain.json.entity.QDynamicJson.dynamicJson;

@Repository
@RequiredArgsConstructor
public class DynamicFormCustomRepositoryImpl implements DynamicFormCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public long deleteByFormId(Long formId) {
        return queryFactory
                .delete(dynamicForm)
                .where(dynamicForm.form.id.eq(formId))
                .execute();
    }

    @Override
    public List<DynamicFormDto> findAllByFormIdWithJson(Long formId) {
        return queryFactory
                .select(Projections.constructor(
                        DynamicFormDto.class,
                        dynamicForm.id,
                        dynamicForm.formType,
                        dynamicForm.title,
                        dynamicJson.jsonData,
                        dynamicForm.requiredStatus,
                        dynamicJson.otherJson,
                        dynamicForm.form.id
                ))
                .from(dynamicForm)
                .join(dynamicJson).on(
                        dynamicJson.recordId.eq(dynamicForm.id),
                        dynamicJson.dynamicJsonType.eq(DynamicJsonType.FORM)
                )
                .where(dynamicForm.form.id.eq(formId))
                .orderBy(dynamicForm.id.asc())
                .fetch();
    }
}