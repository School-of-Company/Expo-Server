package team.startup.expo.domain.participant.repository.custom.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
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
        int size = pageable.getPageSize();

        Long totalElement = queryFactory
                .select(standardParticipant.count())
                .from(standardParticipant)
                .join(standardParticipantParticipation).on(standardParticipantParticipation.standardParticipant.eq(standardParticipant))
                .where(
                        standardParticipant.expo.id.eq(expoId)
                                .and(standardParticipantParticipation.attendanceDate.eq(date))
                )
                .fetchOne();

        int totalPage = (int) ((totalElement + size - 1) / size);

        List<StandardParticipant> participants = queryFactory
                .selectFrom(standardParticipant)
                .join(standardParticipantParticipation).on(standardParticipantParticipation.standardParticipant.eq(standardParticipant))
                .where(
                        standardParticipant.expo.id.eq(expoId)
                                .and(standardParticipantParticipation.attendanceDate.eq(date))
                )
                .offset(pageable.getOffset())
                .limit(size)
                .fetch();

        List<GetParticipantInfoResponseDto> getParticipantInfoResponseDto = participants.stream()
                .map(participant -> GetParticipantInfoResponseDto.builder()
                        .id(participant.getId())
                        .name(participant.getName())
                        .informationStatus(participant.getPersonalInformationStatus())
                        .phoneNumber(participant.getPhoneNumber())
                        .build())
                .toList();

        return ParticipantResponseDto.builder()
                .info(ParticipantResponseDto.Info.builder()
                        .totalPage(totalPage)
                        .totalElement(totalElement.intValue())
                        .build())
                .participants(getParticipantInfoResponseDto)
                .build();

    }
}