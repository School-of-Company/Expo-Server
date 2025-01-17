package team.startup.expo.domain.standard.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.application.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.attendance.exception.NotFoundStandardProgramException;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.ParticipantRepository;
import team.startup.expo.domain.sms.exception.NotFoundParticipantException;
import team.startup.expo.domain.standard.entity.StandardProgram;
import team.startup.expo.domain.standard.entity.StandardProgramUser;
import team.startup.expo.domain.standard.presentation.dto.request.ApplicationStandardProListRequestDto;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.standard.repository.StandardProgramUserRepository;
import team.startup.expo.domain.standard.service.ApplicationStandardProListService;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class ApplicationStandardProListServiceImpl implements ApplicationStandardProListService {

    private final StandardProgramRepository standardProgramRepository;
    private final StandardProgramUserRepository standardProgramUserRepository;
    private final ParticipantRepository participantRepository;
    private final ExpoRepository expoRepository;
    private final TrainingProgramUserRepository trainingProgramUserRepository;

    public void execute(String expoId, ApplicationStandardProListRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        StandardParticipant standardParticipant = participantRepository.findByPhoneNumberAndExpo(dto.getPhoneNumber(), expo)
                .orElseThrow(NotFoundParticipantException::new);

        dto.getStandardProIds().forEach(standardProId -> {saveStandardProUser(standardParticipant, standardProId);});
    }

    private void saveStandardProUser(StandardParticipant standardParticipant, Long standardProId) {
        StandardProgram standardProgram = standardProgramRepository.findById(standardProId)
                .orElseThrow(NotFoundStandardProgramException::new);

        if (standardProgramUserRepository.existsByStandardParticipantAndStandardProgram(standardParticipant, standardProgram))
            throw new AlreadyApplicationUserException();

        StandardProgramUser standardProgramUser = StandardProgramUser.builder()
                .status(false)
                .standardParticipant(standardParticipant)
                .standardProgram(standardProgram)
                .build();

        standardProgramUserRepository.save(standardProgramUser);
    }
}
