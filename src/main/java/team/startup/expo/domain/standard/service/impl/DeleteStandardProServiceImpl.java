package team.startup.expo.domain.standard.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.attendance.exception.NotFoundStandardProgramException;
import team.startup.expo.domain.standard.StandardProgram;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.standard.repository.StandardProgramUserRepository;
import team.startup.expo.domain.standard.service.DeleteStandardProService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class DeleteStandardProServiceImpl implements DeleteStandardProService {

    private final StandardProgramRepository standardProgramRepository;
    private final StandardProgramUserRepository standardProgramUserRepository;

    public void execute(Long standardProId) {
        StandardProgram standardProgram = standardProgramRepository.findById(standardProId)
                .orElseThrow(NotFoundStandardProgramException::new);

        standardProgramUserRepository.deleteByStandardProgram(standardProgram);
        standardProgramRepository.delete(standardProgram);
    }
}
