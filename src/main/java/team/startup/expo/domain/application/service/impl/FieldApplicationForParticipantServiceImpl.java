package team.startup.expo.domain.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.application.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.application.presentation.dto.request.ApplicationForParticipantRequestDto;
import team.startup.expo.domain.application.service.FieldApplicationForParticipantService;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.sms.event.SendQrEvent;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class FieldApplicationForParticipantServiceImpl implements FieldApplicationForParticipantService {

    private final ExpoRepository expoRepository;
    private final StandardParticipantRepository standardParticipantRepository;
    private final TraineeRepository traineeRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void execute(String expoId, ApplicationForParticipantRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (standardParticipantRepository.existsByPhoneNumberAndExpo(dto.getPhoneNumber(), expo) || traineeRepository.existsByPhoneNumberAndExpo(dto.getPhoneNumber(), expo))
            throw new AlreadyApplicationUserException();

        saveParticipant(expo, dto);

        applicationEventPublisher.publishEvent(new SendQrEvent(expoId, dto.getPhoneNumber(), Authority.ROLE_STANDARD));
    }

    private void saveParticipant(Expo expo, ApplicationForParticipantRequestDto dto) {
        StandardParticipant standardParticipant = StandardParticipant.builder()
                .name(dto.getName())
                .phoneNumber(dto.getPhoneNumber())
                .authority(Authority.ROLE_STANDARD)
                .attendanceStatus(false)
                .informationJson(dto.getInformationJson())
                .applicationType(ApplicationType.FIELD)
                .personalInformationStatus(dto.getPersonalInformationStatus())
                .expo(expo)
                .build();

        standardParticipantRepository.save(standardParticipant);
    }
}
