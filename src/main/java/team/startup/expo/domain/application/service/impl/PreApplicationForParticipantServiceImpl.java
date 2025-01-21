package team.startup.expo.domain.application.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.application.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.application.presentation.dto.request.ApplicationForParticipantRequestDto;
import team.startup.expo.domain.application.service.PreApplicationForParticipantService;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.ParticipantRepository;
import team.startup.expo.domain.trainee.entity.ApplicationType;
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
        StandardParticipant standardParticipant = StandardParticipant.builder()
                .name(dto.getName())
                .phoneNumber(dto.getPhoneNumber())
                .authority(Authority.ROLE_STANDARD)
                .attendanceStatus(false)
                .informationJson(dto.getInformationJson())
                .applicationType(ApplicationType.PRE)
                .personalInformationStatus(dto.getPersonalInformationStatus())
                .expo(expo)
                .build();

        participantRepository.save(standardParticipant);
    }
}
