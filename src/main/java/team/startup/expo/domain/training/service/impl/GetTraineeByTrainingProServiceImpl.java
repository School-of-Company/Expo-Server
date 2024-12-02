package team.startup.expo.domain.training.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.training.TrainingProgram;
import team.startup.expo.domain.training.exception.NotFoundTrainingProgramException;
import team.startup.expo.domain.training.presentation.dto.response.GetTrainingProTraineeResponseDto;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.domain.training.service.GetTraineeByTrainingProService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetTraineeByTrainingProServiceImpl implements GetTraineeByTrainingProService {

    private final TrainingProgramUserRepository trainingProgramUserRepository;
    private final TrainingProgramRepository trainingProgramRepository;

    public List<GetTrainingProTraineeResponseDto> execute(Long trainingProId) {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingProId)
                .orElseThrow(NotFoundTrainingProgramException::new);

        return trainingProgramUserRepository.findByTrainingProgram(trainingProgram).stream()
                .map(trainingProgramUser -> GetTrainingProTraineeResponseDto.builder()
                        .id(trainingProgramUser.getId())
                        .name(trainingProgramUser.getTrainee().getName())
                        .organization(trainingProgramUser.getTrainee().getOrganization())
                        .position(trainingProgramUser.getTrainee().getPosition())
                        .programName(trainingProgramUser.getTrainingProgram().getTitle())
                        .status(trainingProgramUser.getStatus())
                        .entryTime(trainingProgramUser.getEntryTime())
                        .leaveTime(trainingProgramUser.getLeaveTime())
                        .build())
                .toList();
    }
}
