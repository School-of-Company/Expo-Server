package team.startup.expo.domain.expo.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.Admin;
import team.startup.expo.domain.admin.util.UserUtil;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.exception.NotMatchAdminException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.expo.service.DeleteExpoService;
import team.startup.expo.domain.participant.repository.ParticipantRepository;
import team.startup.expo.domain.standard.StandardProgram;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.standard.repository.StandardProgramUserRepository;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.training.TrainingProgram;
import team.startup.expo.domain.training.TrainingProgramUser;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.global.annotation.TransactionService;

import java.util.List;

@TransactionService
@RequiredArgsConstructor
public class DeleteExpoServiceImpl implements DeleteExpoService {

    private final ExpoRepository expoRepository;
    private final StandardProgramRepository standardProgramRepository;
    private final StandardProgramUserRepository standardProgramUserRepository;
    private final TrainingProgramUserRepository trainingProgramUserRepository;
    private final ParticipantRepository participantRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final UserUtil userUtil;

    public void execute(String expoId) {
        Admin admin = userUtil.getCurrentUser();

        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (expo.getAdmin() != admin) {
            throw new NotMatchAdminException();
        }

        List<TrainingProgram> trainingPrograms = trainingProgramRepository.findByExpo(expo);

        trainingPrograms.forEach(trainingProgramUserRepository::deleteByTrainingProgram);

        List<StandardProgram> standardPrograms = standardProgramRepository.findByExpo(expo);

        standardPrograms.forEach(standardProgramUserRepository::deleteByStandardProgram);

        standardProgramRepository.deleteByExpo(expo);
        trainingProgramRepository.deleteByExpo(expo);
        participantRepository.deleteByExpo(expo);
        traineeRepository.deleteByExpo(expo);
        expoRepository.delete(expo);
    }
}
