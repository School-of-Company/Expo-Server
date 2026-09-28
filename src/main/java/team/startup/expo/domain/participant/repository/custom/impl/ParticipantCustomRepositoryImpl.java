package team.startup.expo.domain.participant.repository.custom.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.startup.expo.domain.participant.presentation.dto.response.GetParticipantInfoResponseDto;
import team.startup.expo.domain.participant.presentation.dto.response.ParticipantResponseDto;
import team.startup.expo.domain.participant.repository.custom.ParticipantCustomRepository;

import java.time.LocalDate;
import java.util.List;

import static team.startup.expo.domain.participant.entity.QStandardParticipant.standardParticipant;
import static team.startup.expo.domain.participant.entity.QStandardParticipantParticipation.standardParticipantParticipation;

@Repository
@RequiredArgsConstructor
public class ParticipantCustomRepositoryImpl implements ParticipantCustomRepository {

    private final JPAQueryFactory queryFactory;

    public ParticipantResponseDto searchParticipants(String expoId, Pageable pageable, LocalDate date) {
        int size = pageable.getPageSize();

        BooleanExpression condition = standardParticipant.expo.id.eq(expoId)
                .and(standardParticipantParticipation.attendanceDate.eq(date));

        Long totalElement = queryFactory
                .select(standardParticipant.id.count())
                .from(standardParticipant)
                .join(standardParticipantParticipation).on(standardParticipantParticipation.standardParticipant.eq(standardParticipant))
                .where(condition)
                .fetchOne();

        int totalPage = (int) ((totalElement + size - 1) / size);

        List<Long> pagedIds = queryFactory
                .select(standardParticipant.id)
                .from(standardParticipant)
                .join(standardParticipantParticipation).on(standardParticipantParticipation.standardParticipant.eq(standardParticipant))
                .where(condition)
                .orderBy(standardParticipant.id.asc())
                .offset(pageable.getOffset())
                .limit(size)
                .fetch();

        List<GetParticipantInfoResponseDto> participants = pagedIds.isEmpty()
                ? List.of()
                : queryFactory
                .select(Projections.constructor(
                        GetParticipantInfoResponseDto.class,
                        standardParticipant.id,
                        standardParticipant.name,
                        standardParticipant.phoneNumber,
                        standardParticipant.personalInformationStatus
                ))
                .from(standardParticipant)
                .where(standardParticipant.id.in(pagedIds))
                .orderBy(standardParticipant.id.asc())
                .fetch();

        return ParticipantResponseDto.builder()
                .info(ParticipantResponseDto.Info.builder()
                        .totalPage(totalPage)
                        .totalElement(totalElement.intValue())
                        .build())
                .participants(participants)
                .build();

    }
}