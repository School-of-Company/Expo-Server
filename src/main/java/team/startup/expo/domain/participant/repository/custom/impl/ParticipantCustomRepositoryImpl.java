package team.startup.expo.domain.participant.repository.custom.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.startup.expo.domain.participant.presentation.dto.response.GetParticipantInfoResponseDto;
import team.startup.expo.domain.participant.presentation.dto.response.ParticipantResponseDto;
import team.startup.expo.domain.participant.repository.custom.ParticipantCustomRepository;
import team.startup.expo.domain.trainee.entity.ApplicationType;

import java.time.LocalDate;
import java.util.List;

import static team.startup.expo.domain.expo.entity.QExpo.expo;
import static team.startup.expo.domain.participant.entity.QStandardParticipant.standardParticipant;
import static team.startup.expo.domain.participant.entity.QStandardParticipantParticipation.standardParticipantParticipation;

@Repository
@RequiredArgsConstructor
public class ParticipantCustomRepositoryImpl implements ParticipantCustomRepository {
    private final JPAQueryFactory queryFactory;

    public ParticipantResponseDto searchParticipants(String expoId, ApplicationType type, String name, Pageable pageable, LocalDate date) {
        List<GetParticipantInfoResponseDto> participants = queryFactory
                .select(Projections.constructor(GetParticipantInfoResponseDto.class,
                        standardParticipant.id,
                        standardParticipant.name,
                        standardParticipant.phoneNumber,
                        standardParticipant.personalInformationStatus))
                .from(standardParticipant)
                .join(standardParticipant.expo, expo)
                .join(standardParticipantParticipation).on(standardParticipantParticipation.standardParticipant.eq(standardParticipant))
                .where(
                        expoIdEq(expoId),
                        typeEq(type),
                        nameContains(name),
                        dateEq(date, expo.startedDay, expo.finishedDay))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(standardParticipant.count())
                .from(standardParticipant)
                .join(standardParticipant.expo, expo)
                .join(standardParticipantParticipation).on(standardParticipantParticipation.standardParticipant.eq(standardParticipant))
                .where(
                        expoIdEq(expoId),
                        typeEq(type),
                        nameContains(name),
                        dateEq(date, expo.startedDay, expo.finishedDay))
                .fetchOne();

        int totalElements = total != null ? total.intValue() : 0;
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        return ParticipantResponseDto.builder()
                .info(ParticipantResponseDto.Info.builder()
                        .totalPage(totalPages)
                        .totalElement(totalElements)
                        .build())
                .participants(participants)
                .build();
    }

    private BooleanExpression expoIdEq(String expoId) {
        return expoId != null ? expo.id.eq(expoId) : null;
    }

    private BooleanExpression typeEq(ApplicationType type) {
        return type != null ? standardParticipant.applicationType.eq(type) : null;
    }

    private BooleanExpression nameContains(String name) {
        return name != null && !name.isBlank() ? standardParticipant.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression dateEq(LocalDate date, StringPath startedDay, StringPath finishedDay) {
        if (date == null || startedDay == null || finishedDay == null) {
            return null;
        }

        DateExpression<LocalDate> startedDayAsDate = Expressions.dateTemplate(
                LocalDate.class,
                "DATE(STR_TO_DATE({0}, '%Y-%m-%d %H:%i:%s'))",
                startedDay
        );
        DateExpression<LocalDate> finishedDayAsDate = Expressions.dateTemplate(
                LocalDate.class,
                "DATE(STR_TO_DATE({0}, '%Y-%m-%d %H:%i:%s'))",
                finishedDay
        );

        return standardParticipantParticipation.attendanceDate.eq(date)
                .and(standardParticipantParticipation.attendanceDate.goe(startedDayAsDate))
                .and(standardParticipantParticipation.attendanceDate.loe(finishedDayAsDate));
    }
}