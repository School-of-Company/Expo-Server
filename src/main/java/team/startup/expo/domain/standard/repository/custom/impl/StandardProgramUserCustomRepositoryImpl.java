package team.startup.expo.domain.standard.repository.custom.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.startup.expo.domain.standard.entity.StandardProgramUser;
import team.startup.expo.domain.standard.repository.custom.StandardProgramUserCustomRepository;

import java.util.List;

import static team.startup.expo.domain.standard.entity.QStandardProgramUser.standardProgramUser;

@Repository
@RequiredArgsConstructor
public class StandardProgramUserCustomRepositoryImpl implements StandardProgramUserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StandardProgramUser> findByStandardProgramIdWithFetch(Long standardProId) {
        return queryFactory
                .selectFrom(standardProgramUser)
                .join(standardProgramUser.standardProgram).fetchJoin()
                .join(standardProgramUser.standardParticipant).fetchJoin()
                .where(standardProgramUser.standardProgram.id.eq(standardProId))
                .fetch();
    }
}