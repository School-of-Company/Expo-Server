package team.startup.expo.domain.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.application.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.application.presentation.dto.request.ApplicationForTraineeRequestDto;
import team.startup.expo.domain.application.service.FieldApplicationForTraineeService;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.sms.event.SendQrEvent;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class FieldApplicationForTraineeServiceImpl implements FieldApplicationForTraineeService {

    private final TraineeRepository traineeRepository;
    private final ExpoRepository expoRepository;
    private final StandardParticipantRepository standardParticipantRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void execute(String expoId, ApplicationForTraineeRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (standardParticipantRepository.existsByPhoneNumberAndExpo(dto.getPhoneNumber(), expo) || traineeRepository.existsByPhoneNumberAndExpo(dto.getPhoneNumber(), expo))
            throw new AlreadyApplicationUserException();

        saveTrainee(dto, expo);

        applicationEventPublisher.publishEvent(new SendQrEvent(expoId, dto.getPhoneNumber(), Authority.ROLE_TRAINEE));
    }

    private void saveTrainee(ApplicationForTraineeRequestDto dto, Expo expo) {
        Trainee trainee = Trainee.builder()
                .trainingId(dto.getTrainingId())
                .phoneNumber(dto.getPhoneNumber())
                .authority(Authority.ROLE_TRAINEE)
                .name(dto.getName())
                .applicationType(ApplicationType.FIELD)
                .informationJson(dto.getInformationJson())
                .personalInformationStatus(dto.getPersonalInformationStatus())
                .attendanceStatus(false)
                .expo(expo)
                .build();

        traineeRepository.save(trainee);
    }
}
