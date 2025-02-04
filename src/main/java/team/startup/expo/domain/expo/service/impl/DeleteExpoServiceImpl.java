package team.startup.expo.domain.expo.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.expo.service.DeleteExpoService;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.survey.management.repository.SurveyRepository;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class DeleteExpoServiceImpl implements DeleteExpoService {

    private final ExpoRepository expoRepository;
    private final StandardProgramRepository standardProgramRepository;
    private final StandardParticipantRepository standardParticipantRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final FormRepository formRepository;
    private final SurveyRepository surveyRepository;

    public void execute(String expoId) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        deleteTrainingProgram(expo);
        deleteStandardProgram(expo);
        deleteForm(expo);
        deleteSurvey(expo);
        deleteStandardParticipant(expo);
        deleteTrainee(expo);
        deleteExpo(expoId);
    }

    private void deleteTrainingProgram(Expo expo) {
        trainingProgramRepository.deleteByExpo(expo);
    }

    private void deleteStandardProgram(Expo expo) {
        standardProgramRepository.deleteByExpo(expo);
    }

    private void deleteStandardParticipant(Expo expo) {
        standardParticipantRepository.deleteByExpo(expo);
    }

    private void deleteTrainee(Expo expo) {
        traineeRepository.deleteByExpo(expo);
    }

    private void deleteForm(Expo expo) {
        formRepository.deleteByExpo(expo);
    }

    private void deleteSurvey(Expo expo) {
        surveyRepository.deleteByExpo(expo);
    }

    private void deleteExpo(String expoId) {
        expoRepository.deleteById(expoId);
    }
}
