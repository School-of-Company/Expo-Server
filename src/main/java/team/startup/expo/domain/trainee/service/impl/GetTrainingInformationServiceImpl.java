package team.startup.expo.domain.trainee.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.trainee.presentation.dto.response.GetTraineeInformationResponseDto;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.trainee.service.GetTrainingInformationService;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;
import java.util.stream.Collectors;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetTrainingInformationServiceImpl implements GetTrainingInformationService {

    private final TraineeRepository traineeRepository;

    public List<GetTraineeInformationResponseDto> execute() {
        return traineeRepository.findAll().stream()
                .map(trainee -> GetTraineeInformationResponseDto.builder()
                        .id(trainee.getId())
                        .name(trainee.getName())
                        .trainingId(trainee.getTrainingId())
                        .phoneNumber(trainee.getPhoneNumber())
                        .organization(trainee.getOrganization())
                        .schoolLevel(trainee.getSchoolLevel())
                        .position(trainee.getPosition())
                        .laptopStatus(trainee.getLaptopStatus())
                        .informationStatus(trainee.getInformationStatus())
                        .participationType(trainee.getParticipationType())
                        .build()
                ).toList();
    }
}
