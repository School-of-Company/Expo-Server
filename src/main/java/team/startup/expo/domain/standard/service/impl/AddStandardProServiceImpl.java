package team.startup.expo.domain.standard.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.util.UserUtil;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.standard.StandardProgram;
import team.startup.expo.domain.standard.presentation.dto.request.AddStandardProRequestDto;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.standard.service.AddStandardProService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class AddStandardProServiceImpl implements AddStandardProService {

    private final StandardProgramRepository standardProgramRepository;
    private final ExpoRepository expoRepository;

    public void execute(Long expoId, AddStandardProRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        saveStandardProgram(expo, dto);
    }

    private void saveStandardProgram(Expo expo, AddStandardProRequestDto dto) {
        StandardProgram standardProgram = StandardProgram.builder()
                .title(dto.getTitle())
                .startedAt(dto.getStartedAt())
                .endedAt(dto.getEndedAt())
                .expo(expo)
                .build();

        standardProgramRepository.save(standardProgram);
    }
}
