package team.startup.expo.domain.form.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.Authority;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.form.presentation.dto.request.ApplicationForParticipantRequestDto;
import team.startup.expo.domain.form.service.PreApplicationForParticipantService;
import team.startup.expo.domain.participant.ExpoParticipant;
import team.startup.expo.domain.participant.repository.ParticipantRepository;
import team.startup.expo.domain.trainee.ParticipationType;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class PreApplicationForParticipantServiceImpl implements PreApplicationForParticipantService {

    private final ExpoRepository expoRepository;
    private final ParticipantRepository participantRepository;
    private final TraineeRepository traineeRepository;

    public void execute(String expoId, ApplicationForParticipantRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (participantRepository.existsByPhoneNumberAndExpo(dto.getPhoneNumber(), expo) || traineeRepository.existsByPhoneNumberAndExpo(dto.getPhoneNumber(), expo))
            throw new AlreadyApplicationUserException();

        saveParticipant(expo, dto);
    }

    private void saveParticipant(Expo expo, ApplicationForParticipantRequestDto dto) {
        ExpoParticipant expoParticipant = ExpoParticipant.builder()
                .name(dto.getName())
                .affiliation(dto.getAffiliation())
                .phoneNumber(dto.getPhoneNumber())
                .authority(Authority.ROLE_STANDARD)
                .attendanceStatus(false)
                .participationType(ParticipationType.PRE)
                .informationStatus(dto.getInformationStatus())
                .position(dto.getPosition())
                .expo(expo)
                .build();

        participantRepository.save(expoParticipant);
    }
}
