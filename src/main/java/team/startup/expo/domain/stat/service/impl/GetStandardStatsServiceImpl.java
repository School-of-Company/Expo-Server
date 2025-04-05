package team.startup.expo.domain.stat.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.participant.entity.SchoolLevel;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.stat.presentation.dto.response.StandardParticipantStatsResponse;
import team.startup.expo.domain.stat.service.GetStandardStatsService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetStandardStatsServiceImpl implements GetStandardStatsService {

    private final StandardParticipantRepository standardParticipantRepository;
    private final ExpoRepository expoRepository;

    public StandardParticipantStatsResponse execute(String expoId) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        List<StandardParticipant> expoParticipant = standardParticipantRepository.findByExpo(expo);

        return StandardParticipantStatsResponse.builder()
                .elementary(mapElementaryDto(expoParticipant))
                .middle(mapMiddleDto(expoParticipant))
                .high(mapHighDto(expoParticipant))
                .kindergarten(mapKindergartenDto(expoParticipant))
                .other(mapOtherDto(expoParticipant))
                .build();
    }

    private StandardParticipantStatsResponse.ElementaryDto mapElementaryDto(List<StandardParticipant> participants) {
        int elementaryCount = (int) participants.stream()
                .filter(sp -> sp.getSchoolLevel().equals(SchoolLevel.ELEMENTARY))
                .count();

        int allCount = participants.size();
        float elementaryPercent = (float) Math.round((elementaryCount * 100.0 / allCount));

        return StandardParticipantStatsResponse.ElementaryDto.builder()
                .number(elementaryCount)
                .percent(elementaryPercent)
                .build();
    }

    private StandardParticipantStatsResponse.MiddleDto mapMiddleDto(List<StandardParticipant> participants) {
        int middleCount = (int) participants.stream()
                .filter(sp -> sp.getSchoolLevel().equals(SchoolLevel.MIDDLE))
                .count();

        int allCount = participants.size();
        float middlePercent = (float) Math.round(middleCount * 100.0 / allCount);

        return StandardParticipantStatsResponse.MiddleDto.builder()
                .number(middleCount)
                .percent(middlePercent)
                .build();
    }

    private StandardParticipantStatsResponse.HighDto mapHighDto(List<StandardParticipant> participants) {
        int highCount = (int) participants.stream()
                .filter(sp -> sp.getSchoolLevel().equals(SchoolLevel.HIGH))
                .count();

        int allCount = participants.size();
        float highPercent = (float) Math.round(highCount * 100.0 / allCount);

        return StandardParticipantStatsResponse.HighDto.builder()
                .number(highCount)
                .percent(highPercent)
                .build();
    }

    private StandardParticipantStatsResponse.KindergartenDto mapKindergartenDto(List<StandardParticipant> participants) {
        int kindergartenCount = (int) participants.stream()
                .filter(sp -> sp.getSchoolLevel().equals(SchoolLevel.KINDERGARTEN))
                .count();

        int allCount = participants.size();
        float kindergartenPercent = (float) Math.round(kindergartenCount * 100.0 / allCount);

        return StandardParticipantStatsResponse.KindergartenDto.builder()
                .number(kindergartenCount)
                .percent(kindergartenPercent)
                .build();
    }

    private StandardParticipantStatsResponse.OtherDto mapOtherDto(List<StandardParticipant> participants) {
        int otherCount = (int) participants.stream()
                .filter(sp -> sp.getSchoolLevel().equals(SchoolLevel.OTHER))
                .count();

        int allCount = participants.size();
        float otherPercent = (float) Math.round(otherCount * 100.0 / allCount);

        return StandardParticipantStatsResponse.OtherDto.builder()
                .number(otherCount)
                .percent(otherPercent)
                .build();
    }
}
