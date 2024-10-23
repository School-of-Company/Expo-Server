package team.startup.expo.domain.trainee.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.trainee.presentation.dto.response.GetTraineeInformationResponseDto;
import team.startup.expo.domain.trainee.presentation.dto.response.GetTrainingProgramResponseDto;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.trainee.service.GetTrainingInformationService;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;
import java.util.stream.Collectors;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetTrainingInformationServiceImpl implements GetTrainingInformationService {

    private final TrainingProgramUserRepository trainingProgramUserRepository;
    private final TraineeRepository traineeRepository;

    public List<GetTraineeInformationResponseDto> execute() {
        return traineeRepository.findAll().stream()
                .map(trainee -> {
                    List<GetTrainingProgramResponseDto> programResponseDtos = trainingProgramUserRepository.findByTrainee(trainee).stream()
                            .map(trainingProgramUser -> GetTrainingProgramResponseDto.builder()
                                    .programName(trainingProgramUser.getTrainingProgram().getTitle())
                                    .build())
                            .toList();

                    return GetTraineeInformationResponseDto.builder()
                            .name(trainee.getName())
                            .trainingProgram(programResponseDtos)
                            .trainingId(trainee.getTrainingId())
                            .laptopStatus(trainee.getLaptopStatus())
                            .build();
                }).collect(Collectors.toList());
    }
}
