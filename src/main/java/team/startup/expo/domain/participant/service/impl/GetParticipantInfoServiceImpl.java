package team.startup.expo.domain.participant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.participant.exception.InvalidDateRangeException;
import team.startup.expo.domain.participant.exception.InvalidPageNumberException;
import team.startup.expo.domain.participant.exception.InvalidPageSizeException;
import team.startup.expo.domain.participant.presentation.dto.response.ParticipantResponseDto;
import team.startup.expo.domain.participant.repository.custom.ParticipantCustomRepository;
import team.startup.expo.domain.participant.service.GetParticipantInfoService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetParticipantInfoServiceImpl implements GetParticipantInfoService {

    private final ParticipantCustomRepository participantRepositoryCustom;
    private final ExpoRepository expoRepository;

    public ParticipantResponseDto execute(String expoId, Pageable pageable, LocalDate date) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        validateParameters(pageable);

        LocalDate targetDate = date != null ? date : LocalDate.now();

        validateDateRange(expoId, targetDate);

        return participantRepositoryCustom.searchParticipants(expo.getId(), pageable, targetDate);

    }

    private void validateDateRange(String expoId, LocalDate date) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startedDate = LocalDate.parse(expo.getStartedDay(), formatter);
        LocalDate finishedDate = LocalDate.parse(expo.getFinishedDay(), formatter);

        if (date.isBefore(startedDate) || date.isAfter(finishedDate)) {
            throw new InvalidDateRangeException();
        }
    }

    private void validateParameters(Pageable pageable) {
        if (pageable.getPageNumber() < 0) {
            throw new InvalidPageNumberException();
        }
        if (pageable.getPageSize() < 0) {
            throw new InvalidPageSizeException();
        }
    }
}
