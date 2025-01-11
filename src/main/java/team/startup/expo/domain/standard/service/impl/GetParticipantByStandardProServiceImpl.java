package team.startup.expo.domain.standard.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.attendance.exception.NotFoundStandardProgramException;
import team.startup.expo.domain.standard.StandardProgram;
import team.startup.expo.domain.standard.presentation.dto.response.GetStandardProParticipantResponseDto;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.standard.repository.StandardProgramUserRepository;
import team.startup.expo.domain.standard.service.GetParticipantByStandardProService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetParticipantByStandardProServiceImpl implements GetParticipantByStandardProService {

    private final StandardProgramRepository standardProgramRepository;
    private final StandardProgramUserRepository standardProgramUserRepository;

    public List<GetStandardProParticipantResponseDto> execute(Long standardProId) {
        StandardProgram standardProgram = standardProgramRepository.findById(standardProId)
                .orElseThrow(NotFoundStandardProgramException::new);

        return standardProgramUserRepository.findByStandardProgram(standardProgram).stream()
                .map(standardProgramUser ->
                    GetStandardProParticipantResponseDto.builder()
                            .id(standardProgramUser.getId())
                            .name(standardProgramUser.getStandardParticipant().getName())
                            .programName(standardProgramUser.getStandardProgram().getTitle())
                            .entryTime(standardProgramUser.getEntryTime())
                            .leaveTime(standardProgramUser.getLeaveTime())
                            .status(standardProgramUser.getStatus())
                            .build()
                ).toList();
    }
}
