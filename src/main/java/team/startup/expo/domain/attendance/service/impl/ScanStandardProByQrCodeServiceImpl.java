package team.startup.expo.domain.attendance.service.impl;

import lombok.RequiredArgsConstructor;
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

        StandardProgramUser standardProgramUser = standardProgramUserRepository.findByStandardProgramAndExpoParticipant(standardProgram, expoParticipant)
                .orElseThrow(NotFoundStandardProgramUserException::new);

        if (standardProgramUser.getEntryTime() == null) {
            saveEntryStandardProgramUser(standardProgramUser, standardProgram, expoParticipant);
        } else if (standardProgramUser.getLeaveTime() == null) {
            saveLeaveStandardProgramUser(standardProgramUser, standardProgram, expoParticipant);
        }
    }

    private void saveEntryStandardProgramUser(StandardProgramUser user,StandardProgram standardProgram, ExpoParticipant expoParticipant) {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        StandardProgramUser standardProgramUser = StandardProgramUser.builder()
                .id(user.getId())
                .status(true)
                .entryTime(String.valueOf(now))
                .standardProgram(standardProgram)
                .expoParticipant(expoParticipant)
                .build();

        standardProgramUserRepository.save(standardProgramUser);
    }

    private void saveLeaveStandardProgramUser(StandardProgramUser user,StandardProgram standardProgram, ExpoParticipant expoParticipant) {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        StandardProgramUser standardProgramUser = StandardProgramUser.builder()
                .id(user.getId())
                .status(true)
                .entryTime(user.getEntryTime())
                .leaveTime(String.valueOf(now))
                .standardProgram(standardProgram)
                .expoParticipant(expoParticipant)
                .build();

        standardProgramUserRepository.save(standardProgramUser);
    }
}
