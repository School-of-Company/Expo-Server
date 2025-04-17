package team.startup.expo.domain.participant.repository.custom.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.startup.expo.domain.participant.presentation.dto.response.GetParticipantInfoResponseDto;
import team.startup.expo.domain.participant.presentation.dto.response.ParticipantResponseDto;
import team.startup.expo.domain.participant.repository.custom.ParticipantCustomRepository;

import java.time.LocalDate;
import java.util.List;

import static team.startup.expo.domain.expo.entity.QExpo.expo;
import static team.startup.expo.domain.participant.entity.QStandardParticipant.standardParticipant;
import static team.startup.expo.domain.participant.entity.QStandardParticipantParticipation.standardParticipantParticipation;

@Repository
@RequiredArgsConstructor
public class ParticipantCustomRepositoryImpl implements ParticipantCustomRepository {
    private final JPAQueryFactory queryFactory;

    public ParticipantResponseDto searchParticipants(String expoId, Pageable pageable, LocalDate date) {
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
                        standardParticipant.expo.id.eq(expoId)
                                .and(standardParticipantParticipation.attendanceDate.eq(date))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(standardParticipant.count())
                .from(standardParticipant)
                .join(standardParticipant.expo, expo)
                .join(standardParticipantParticipation).on(standardParticipantParticipation.standardParticipant.eq(standardParticipant))
                .where()
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
}