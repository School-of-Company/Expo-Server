package team.startup.expo.domain.attendance.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.attendance.exception.NotFoundStandardProgramException;
import team.startup.expo.domain.attendance.exception.NotFoundStandardProgramUserException;
import team.startup.expo.domain.attendance.presentation.dto.request.ScanStandardProRequestDto;
import team.startup.expo.domain.attendance.service.ScanStandardProByQrCodeService;
import team.startup.expo.domain.expo.exception.NotInProgressExpoException;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.sms.exception.NotFoundParticipantException;
import team.startup.expo.domain.standard.entity.StandardProgram;
import team.startup.expo.domain.standard.entity.StandardProgramUser;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.standard.repository.StandardProgramUserRepository;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.date.DateUtil;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@TransactionService
@RequiredArgsConstructor
public class ScanStandardProByQrCodeServiceImpl implements ScanStandardProByQrCodeService {

    private final StandardParticipantRepository standardParticipantRepository;
    private final StandardProgramRepository standardProgramRepository;
    private final StandardProgramUserRepository standardProgramUserRepository;
    private final DateUtil dateUtil;

    public void execute(Long standardProId, ScanStandardProRequestDto dto) {
        StandardParticipant standardParticipant = standardParticipantRepository.findById(dto.getParticipantId())
                .orElseThrow(NotFoundParticipantException::new);

        StandardProgram standardProgram = standardProgramRepository.findById(standardProId)
                .orElseThrow(NotFoundStandardProgramException::new);

        if (!dateUtil.dateComparison(standardProgram.getExpo().getStartedDay(), standardProgram.getExpo().getFinishedDay()))
            throw new NotInProgressExpoException();

        StandardProgramUser standardProgramUser = standardProgramUserRepository.findByStandardProgramAndStandardParticipant(standardProgram, standardParticipant)
                .orElseThrow(NotFoundStandardProgramUserException::new);

        if (standardProgramUser.getEntryTime() == null) {
            saveEntryStandardProgramUser(standardProgramUser, standardProgram, standardParticipant);
        } else if (standardProgramUser.getLeaveTime() == null) {
            saveLeaveStandardProgramUser(standardProgramUser, standardProgram, standardParticipant);
        }
    }

    private void saveEntryStandardProgramUser(StandardProgramUser user,StandardProgram standardProgram, StandardParticipant standardParticipant) {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        StandardProgramUser standardProgramUser = StandardProgramUser.builder()
                .id(user.getId())
                .status(true)
                .entryTime(String.valueOf(now))
                .standardProgram(standardProgram)
                .standardParticipant(standardParticipant)
                .build();

        standardProgramUserRepository.save(standardProgramUser);
    }

    private void saveLeaveStandardProgramUser(StandardProgramUser user,StandardProgram standardProgram, StandardParticipant standardParticipant) {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        StandardProgramUser standardProgramUser = StandardProgramUser.builder()
                .id(user.getId())
                .status(true)
                .entryTime(user.getEntryTime())
                .leaveTime(String.valueOf(now))
                .standardProgram(standardProgram)
                .standardParticipant(standardParticipant)
                .build();

        standardProgramUserRepository.save(standardProgramUser);
    }
}
