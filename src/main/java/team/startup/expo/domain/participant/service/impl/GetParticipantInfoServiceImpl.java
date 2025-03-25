package team.startup.expo.domain.participant.service.impl;

import com.amazonaws.jmespath.InvalidTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import team.startup.expo.domain.participant.exception.InvalidApplicationTypeException;
import team.startup.expo.domain.participant.exception.InvalidExpoIdException;
import team.startup.expo.domain.participant.exception.InvalidPageNumberException;
import team.startup.expo.domain.participant.exception.InvalidPageSizeException;
import team.startup.expo.domain.participant.presentation.dto.response.ParticipantResponseDto;
import team.startup.expo.domain.participant.repository.custom.ParticipantCustomRepository;
import team.startup.expo.domain.participant.service.GetParticipantInfoService;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.time.LocalDate;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetParticipantInfoServiceImpl implements GetParticipantInfoService {

    private final ParticipantCustomRepository participantRepositoryCustom;

    public ParticipantResponseDto execute(
            String expoId, ApplicationType type, String name, Pageable pageable, LocalDate date) {

        validateParameters(expoId, type, pageable);

        LocalDate targetDate = date != null ? date : LocalDate.now();

        return participantRepositoryCustom.searchParticipants(expoId, type, name, pageable, targetDate);

    }

    private void validateParameters(String expoId, ApplicationType type, Pageable pageable) {
        if (expoId == null || expoId.isEmpty()) {
            throw new InvalidExpoIdException();
        }
        if (type == null) {
            throw new InvalidApplicationTypeException();
        }
        if (pageable.getPageNumber() < 0) {
            throw new InvalidPageNumberException();
        }
        if (pageable.getPageSize() < 0) {
            throw new InvalidPageSizeException();
        }
    }
}
