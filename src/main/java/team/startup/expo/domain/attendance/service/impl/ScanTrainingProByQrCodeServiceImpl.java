package team.startup.expo.domain.attendance.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.attendance.exception.NotFoundTrainingProgramUserException;
import team.startup.expo.domain.attendance.presentation.dto.request.ScanTrainingProRequestDto;
import team.startup.expo.domain.attendance.service.ScanTrainingProByQrCodeService;

import java.time.LocalDate;
import java.time.LocalTime;

import team.startup.expo.domain.expo.exception.NotInProgressExpoException;
import team.startup.expo.domain.sms.exception.NotFoundTraineeException;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.training.entity.TrainingProgram;
import team.startup.expo.domain.training.entity.TrainingProgramUser;
import team.startup.expo.domain.training.exception.NotFoundTrainingProgramException;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.date.DateUtil;

import java.time.temporal.ChronoUnit;

@TransactionService
@RequiredArgsConstructor
public class ScanTrainingProByQrCodeServiceImpl implements ScanTrainingProByQrCodeService {

    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingProgramUserRepository trainingProgramUserRepository;
    private final TraineeRepository traineeRepository;
    private final DateUtil dateUtil;

    public void execute(Long trainingProId, ScanTrainingProRequestDto dto) {
        Trainee trainee = traineeRepository.findById(dto.getTraineeId())
                .orElseThrow(NotFoundTraineeException::new);

        TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingProId)
                .orElseThrow(NotFoundTrainingProgramException::new);

        if (!dateUtil.dateComparison(trainingProgram.getExpo().getStartedDay(), trainingProgram.getExpo().getFinishedDay()))
            throw new NotInProgressExpoException();

        TrainingProgramUser trainingProgramUser = trainingProgramUserRepository.findByTraineeAndTrainingProgram(trainee, trainingProgram)
                .orElse(TrainingProgramUser.builder()
                        .status(false)
                        .attendanceDate(LocalDate.now())
                        .trainingProgram(trainingProgram)
                        .trainee(trainee)
                        .build()
                );

        if (trainingProgramUser.getEntryTime() == null) {
            saveEntryTrainingProUser(trainingProgramUser, trainingProgram, trainee);
        } else if (trainingProgramUser.getLeaveTime() == null) {
            saveLeaveTrainingProUser(trainingProgramUser, trainingProgram, trainee);
        }
    }

    private void saveEntryTrainingProUser(TrainingProgramUser user, TrainingProgram trainingProgram, Trainee trainee) {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        TrainingProgramUser trainingProgramUser = TrainingProgramUser.builder()
                .id(user.getId())
                .attendanceDate(user.getAttendanceDate())
                .entryTime(String.valueOf(now))
                .status(true)
                .trainingProgram(trainingProgram)
                .trainee(trainee)
                .build();

        trainingProgramUserRepository.save(trainingProgramUser);
    }

    private void saveLeaveTrainingProUser(TrainingProgramUser user, TrainingProgram trainingProgram, Trainee trainee) {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        TrainingProgramUser trainingProgramUser = TrainingProgramUser.builder()
                .id(user.getId())
                .attendanceDate(user.getAttendanceDate())
                .entryTime(user.getEntryTime())
                .leaveTime(String.valueOf(now))
                .status(true)
                .trainingProgram(trainingProgram)
                .trainee(trainee)
                .build();

        trainingProgramUserRepository.save(trainingProgramUser);
    }
}
