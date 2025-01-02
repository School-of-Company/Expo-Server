package team.startup.expo.domain.standard.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.attendance.exception.NotFoundStandardProgramException;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.standard.StandardProgram;
import team.startup.expo.domain.standard.presentation.dto.response.GetStandardProgramResponseDto;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.standard.service.GetStandardProListService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetStandardProListServiceImpl implements GetStandardProListService {

    private final StandardProgramRepository standardProgramRepository;
    private final ExpoRepository expoRepository;

    public List<GetStandardProgramResponseDto> execute(String expoId) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        return standardProgramRepository.findByExpo(expo).stream()
                .map(standardProgram -> GetStandardProgramResponseDto.builder()
                        .id(standardProgram.getId())
                        .title(standardProgram.getTitle())
                        .startedAt(standardProgram.getStartedAt())
                        .endedAt(standardProgram.getEndedAt())
                        .build()
                ).toList();
    }
}
