package team.startup.expo.domain.standard.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.application.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.attendance.exception.NotFoundStandardProgramException;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.sms.exception.NotFoundParticipantException;
import team.startup.expo.domain.standard.entity.StandardProgram;
import team.startup.expo.domain.standard.entity.StandardProgramUser;
import team.startup.expo.domain.standard.presentation.dto.request.ApplicationStandardProListRequestDto;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.standard.repository.StandardProgramUserRepository;
import team.startup.expo.domain.standard.service.ApplicationStandardProListService;
import team.startup.expo.global.annotation.TransactionService;

import java.util.List;

@TransactionService
@RequiredArgsConstructor
public class ApplicationStandardProListServiceImpl implements ApplicationStandardProListService {

    private final StandardProgramRepository standardProgramRepository;
    private final StandardProgramUserRepository standardProgramUserRepository;
    private final StandardParticipantRepository standardParticipantRepository;
    private final ExpoRepository expoRepository;

    public void execute(String expoId, ApplicationStandardProListRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        StandardParticipant standardParticipant = standardParticipantRepository.findByPhoneNumberAndExpo(dto.getPhoneNumber(), expo)
                .orElseThrow(NotFoundParticipantException::new);

        List<Long> distinctIds = dto.getStandardProIds().stream().distinct().toList();
        List<StandardProgram> programs = standardProgramRepository.findAllByIdIn(distinctIds);
        if (programs.size() != distinctIds.size())
            throw new NotFoundStandardProgramException();

        if (standardProgramUserRepository.existsByStandardParticipantAndStandardProgramIn(standardParticipant, programs))
            throw new AlreadyApplicationUserException();

        List<StandardProgramUser> users = programs.stream()
                .map(program -> StandardProgramUser.builder()
                        .status(false)
                        .standardParticipant(standardParticipant)
                        .standardProgram(program)
                        .build())
                .toList();

        standardProgramUserRepository.saveAll(users);
    }
}
