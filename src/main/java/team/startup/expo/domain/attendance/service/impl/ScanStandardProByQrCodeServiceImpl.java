package team.startup.expo.domain.attendance.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.attendance.exception.AlreadyLeaveProgramUserException;
import team.startup.expo.domain.attendance.exception.NotFoundStandardProgramException;
import team.startup.expo.domain.attendance.exception.NotFoundStandardProgramUserException;
import team.startup.expo.domain.attendance.presentation.dto.request.ScanStandardProRequestDto;
import team.startup.expo.domain.attendance.service.ScanStandardProByQrCodeService;
import team.startup.expo.domain.participant.ExpoParticipant;
import team.startup.expo.domain.participant.repository.ParticipantRepository;
import team.startup.expo.domain.sms.exception.NotFoundParticipantException;
import team.startup.expo.domain.standard.StandardProgram;
import team.startup.expo.domain.standard.StandardProgramUser;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.standard.repository.StandardProgramUserRepository;
import team.startup.expo.global.annotation.TransactionService;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@TransactionService
@RequiredArgsConstructor
public class ScanStandardProByQrCodeServiceImpl implements ScanStandardProByQrCodeService {

    private final ParticipantRepository participantRepository;
    private final StandardProgramRepository standardProgramRepository;
    private final StandardProgramUserRepository standardProgramUserRepository;

    public void execute(Long standardProId, ScanStandardProRequestDto dto) {
        ExpoParticipant expoParticipant = participantRepository.findById(dto.getParticipantId())
                .orElseThrow(NotFoundParticipantException::new);

        StandardProgram standardProgram = standardProgramRepository.findById(standardProId)
                .orElseThrow(NotFoundStandardProgramException::new);

        if (standardProgramUserRepository.existsByExpoParticipantAndStandardProgram(expoParticipant, standardProgram)) {
            StandardProgramUser standardProgramUser = standardProgramUserRepository.findByStandardProgramAndExpoParticipant(standardProgram, expoParticipant)
                    .orElseThrow(NotFoundStandardProgramUserException::new);

            if (standardProgramUser.getLeaveTime() != null)
                throw new AlreadyLeaveProgramUserException();

            standardProgramUser.addLeaveTime(String.valueOf(LocalTime.now().truncatedTo(ChronoUnit.MINUTES)));
        } else {
            saveStandardProgramUser(standardProgram, expoParticipant);
        }
    }

    private void saveStandardProgramUser(StandardProgram standardProgram, ExpoParticipant expoParticipant) {
        StandardProgramUser standardProgramUser = StandardProgramUser.builder()
                .status(true)
                .entryTime(String.valueOf(LocalTime.now().truncatedTo(ChronoUnit.MINUTES)))
                .standardProgram(standardProgram)
                .expoParticipant(expoParticipant)
                .build();

        standardProgramUserRepository.save(standardProgramUser);
    }
}
