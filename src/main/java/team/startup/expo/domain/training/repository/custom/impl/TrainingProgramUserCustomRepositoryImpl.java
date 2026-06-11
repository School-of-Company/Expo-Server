package team.startup.expo.domain.training.repository.custom.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.startup.expo.domain.training.entity.TrainingProgramUser;
import team.startup.expo.domain.training.repository.custom.TrainingProgramUserCustomRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static team.startup.expo.domain.training.entity.QTrainingProgramUser.trainingProgramUser;

@Repository
@RequiredArgsConstructor
public class TrainingProgramUserCustomRepositoryImpl implements TrainingProgramUserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Long, Integer> countTrainingProgramUserByTrainingProIdIn(Collection<Long> trainingProIds) {
        var count = trainingProgramUser.id.count();
        return queryFactory
                .select(trainingProgramUser.trainingProgram.id, trainingProgramUser.id.count())
                .from(trainingProgramUser)
                .where(trainingProgramUser.trainingProgram.id.in(trainingProIds))
                .groupBy(trainingProgramUser.trainingProgram.id)
                .fetch()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> row.get(trainingProgramUser.trainingProgram.id),
                        row -> Math.toIntExact(row.get(count))
                ));
    }

    @Override
    public List<TrainingProgramUser> findByTrainingProgramIdWithFetch(Long trainingProId) {
        return queryFactory
                .selectFrom(trainingProgramUser)
                .join(trainingProgramUser.trainingProgram).fetchJoin()
                .join(trainingProgramUser.trainee).fetchJoin()
                .where(trainingProgramUser.trainingProgram.id.eq(trainingProId))
                .fetch();
    }
}
