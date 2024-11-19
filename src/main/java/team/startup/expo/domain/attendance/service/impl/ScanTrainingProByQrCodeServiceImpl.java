package team.startup.expo.domain.attendance.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.attendance.exception.AlreadyLeaveProgramUserException;
import team.startup.expo.domain.attendance.exception.NotFoundTrainingProgramUserException;
import team.startup.expo.domain.attendance.presentation.dto.request.ScanTrainingProRequestDto;
import team.startup.expo.domain.attendance.service.ScanTrainingProByQrCodeService;
import java.time.LocalTime;
import team.startup.expo.domain.sms.exception.NotFoundTraineeException;
import team.startup.expo.domain.trainee.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.training.TrainingProgram;
import team.startup.expo.domain.training.TrainingProgramUser;
import team.startup.expo.domain.training.exception.NotFoundTrainingProgramException;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.global.annotation.TransactionService;

import java.time.temporal.ChronoUnit;

@TransactionService
@RequiredArgsConstructor
public class ScanTrainingProByQrCodeServiceImpl implements ScanTrainingProByQrCodeService {

    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingProgramUserRepository trainingProgramUserRepository;
    private final TraineeRepository traineeRepository;

    public void execute(Long trainingProId, ScanTrainingProRequestDto dto) {
        Trainee trainee = traineeRepository.findById(dto.getTraineeId())
                .orElseThrow(NotFoundTraineeException::new);

        TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingProId)
                .orElseThrow(NotFoundTrainingProgramException::new);

        if (trainingProgramUserRepository.existsByTraineeAndTrainingProgram(trainee, trainingProgram)) {
            TrainingProgramUser trainingProgramUser = trainingProgramUserRepository.findByTraineeAndTrainingProgram(trainee, trainingProgram)
                    .orElseThrow(NotFoundTrainingProgramUserException::new);

            if (trainingProgramUser.getLeaveTime() != null)
                throw new AlreadyLeaveProgramUserException();

            trainingProgramUser.addLeaveTime(String.valueOf(LocalTime.now().truncatedTo(ChronoUnit.MINUTES)));
        } else {
            saveTrainingProgramUser(trainingProgram, trainee);
        }
    }

    private void saveTrainingProgramUser(TrainingProgram trainingProgram, Trainee trainee) {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        TrainingProgramUser trainingProgramUser = TrainingProgramUser.builder()
                .status(true)
                .entryTime(String.valueOf(now))
                .trainingProgram(trainingProgram)
                .trainee(trainee)
                .build();

        trainingProgramUserRepository.save(trainingProgramUser);
    }
}
