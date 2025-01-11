package team.startup.expo.domain.standard.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.standard.StandardProgram;
import team.startup.expo.domain.standard.presentation.dto.request.AddStandardProRequestDto;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.standard.service.AddStandardProListService;
import team.startup.expo.global.annotation.TransactionService;

import java.util.List;

@TransactionService
@RequiredArgsConstructor
public class AddStandardProListServiceImpl implements AddStandardProListService {

    private final StandardProgramRepository standardProgramRepository;
    private final ExpoRepository expoRepository;

    public void execute(String expoId, List<AddStandardProRequestDto> dtos) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        dtos.forEach(dto -> saveStandardProgram(dto, expo));
    }

    private void saveStandardProgram(AddStandardProRequestDto dto, Expo expo) {
        StandardProgram standardProgram = StandardProgram.builder()
                .title(dto.getTitle())
                .startedAt(String.valueOf(dto.getStartedAt()))
                .endedAt(String.valueOf(dto.getEndedAt()))
                .expo(expo)
                .build();

        standardProgramRepository.save(standardProgram);
    }
}
