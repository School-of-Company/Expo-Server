package team.startup.expo.domain.survey.answer.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.sms.exception.NotFoundTraineeException;
import team.startup.expo.domain.survey.answer.entity.TraineeSurveyAnswer;
import team.startup.expo.domain.survey.answer.exception.AlreadyExistSurveyAnswerException;
import team.startup.expo.domain.survey.answer.presentation.dto.request.SurveyAnswerRequestDto;
import team.startup.expo.domain.survey.answer.repository.TraineeSurveyAnswerRepository;
import team.startup.expo.domain.survey.answer.service.TraineeSurveyAnswerService;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class TraineeSurveyAnswerServiceImpl implements TraineeSurveyAnswerService {

    private final TraineeSurveyAnswerRepository traineeSurveyAnswerRepository;
    private final ExpoRepository expoRepository;
    private final TraineeRepository traineeRepository;

    public void execute(String expoId, SurveyAnswerRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundTraineeException::new);

        Trainee trainee = traineeRepository.findByPhoneNumberAndExpo(dto.getPhoneNumber(), expo)
                .orElseThrow(NotFoundTraineeException::new);

        if (traineeSurveyAnswerRepository.existsByTrainee(trainee))
            throw new AlreadyExistSurveyAnswerException();

        saveTraineeSurveyAnswer(dto, trainee);
    }

    private void saveTraineeSurveyAnswer(SurveyAnswerRequestDto dto, Trainee trainee) {
        TraineeSurveyAnswer answer = TraineeSurveyAnswer.builder()
                .answerJson(dto.getAnswerJson())
                .trainee(trainee)
                .personalInformationStatus(dto.getPersonalInformationStatus())
                .build();

        traineeSurveyAnswerRepository.save(answer);
    }
}
