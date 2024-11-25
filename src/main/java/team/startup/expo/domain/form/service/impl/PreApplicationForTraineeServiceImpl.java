package team.startup.expo.domain.form.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.Authority;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.presentation.dto.request.PreApplicationForTraineeRequestDto;
import team.startup.expo.domain.form.service.PreApplicationForTraineeService;
import team.startup.expo.domain.trainee.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class PreApplicationForTraineeServiceImpl implements PreApplicationForTraineeService {

    private final TraineeRepository traineeRepository;
    private final ExpoRepository expoRepository;

    public void execute(String expoId, PreApplicationForTraineeRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        saveTrainee(dto, expo);
    }

    private void saveTrainee(PreApplicationForTraineeRequestDto dto, Expo expo) {
        Trainee trainee = Trainee.builder()
                .trainingId(dto.getTrainingId())
                .laptopStatus(dto.getLaptopStatus())
                .phoneNumber(dto.getPhoneNumber())
                .authority(Authority.ROLE_TRAINEE)
                .position(dto.getPosition())
                .schoolLevel(dto.getSchoolLevel())
                .organization(dto.getOrganization())
                .name(dto.getName())
                .informationStatus(dto.getInformationStatus())
                .attendanceStatus(false)
                .expo(expo)
                .build();

        traineeRepository.save(trainee);
    }
}
