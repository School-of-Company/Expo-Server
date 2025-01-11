package team.startup.expo.domain.trainee.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.trainee.presentation.dto.response.GetTraineeInformationResponseDto;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.trainee.service.GetTrainingInformationService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetTrainingInformationServiceImpl implements GetTrainingInformationService {

    private final TraineeRepository traineeRepository;
    private final ExpoRepository expoRepository;

    public List<GetTraineeInformationResponseDto> execute(String expoId) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        return traineeRepository.findByExpo(expo).stream()
                .map(trainee -> GetTraineeInformationResponseDto.builder()
                        .id(trainee.getId())
                        .name(trainee.getName())
                        .phoneNumber(trainee.getPhoneNumber())
                        .applicationType(trainee.getApplicationType())
                        .build()
                ).toList();
    }
}
