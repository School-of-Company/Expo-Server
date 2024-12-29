package team.startup.expo.domain.form.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.Authority;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.form.presentation.dto.request.ApplicationForTraineeRequestDto;
import team.startup.expo.domain.form.service.FieldApplicationForTraineeService;
import team.startup.expo.domain.participant.repository.ParticipantRepository;
import team.startup.expo.domain.trainee.ParticipationType;
import team.startup.expo.domain.trainee.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class FieldApplicationForTraineeServiceImpl implements FieldApplicationForTraineeService {

    private final TraineeRepository traineeRepository;
    private final ExpoRepository expoRepository;
    private final ParticipantRepository participantRepository;

    public void execute(String expoId, ApplicationForTraineeRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (participantRepository.existsByPhoneNumberAndExpo(dto.getPhoneNumber(), expo) || traineeRepository.existsByPhoneNumberAndExpo(dto.getPhoneNumber(), expo))
            throw new AlreadyApplicationUserException();

        saveTrainee(dto, expo);
    }

    private void saveTrainee(ApplicationForTraineeRequestDto dto, Expo expo) {
        Trainee trainee = Trainee.builder()
                .trainingId(dto.getTrainingId())
                .laptopStatus(dto.getLaptopStatus())
                .phoneNumber(dto.getPhoneNumber())
                .authority(Authority.ROLE_TRAINEE)
                .position(dto.getPosition())
                .schoolLevel(dto.getSchoolLevel())
                .organization(dto.getOrganization())
                .name(dto.getName())
                .participationType(ParticipationType.FIELD)
                .informationStatus(dto.getInformationStatus())
                .attendanceStatus(false)
                .expo(expo)
                .build();

        traineeRepository.save(trainee);
    }
}
