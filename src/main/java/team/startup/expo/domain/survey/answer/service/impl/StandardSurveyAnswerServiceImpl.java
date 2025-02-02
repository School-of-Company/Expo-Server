package team.startup.expo.domain.survey.answer.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.sms.exception.NotFoundParticipantException;
import team.startup.expo.domain.survey.answer.entity.StandardParticipantSurveyAnswer;
import team.startup.expo.domain.survey.answer.presentation.dto.request.SurveyAnswerRequestDto;
import team.startup.expo.domain.survey.answer.repository.StandardParticipantSurveyAnswerRepository;
import team.startup.expo.domain.survey.answer.service.StandardSurveyAnswerService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class StandardSurveyAnswerServiceImpl implements StandardSurveyAnswerService {

    private final StandardParticipantSurveyAnswerRepository standardparticipantSurveyAnswerRepository;
    private final StandardParticipantRepository participantRepository;
    private final ExpoRepository expoRepository;

    public void execute(String expoId, SurveyAnswerRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        StandardParticipant standardParticipant = participantRepository.findByPhoneNumberAndExpo(dto.getPhoneNumber(), expo)
                .orElseThrow(NotFoundParticipantException::new);

        saveSurveyAnswer(dto.getAnswerJson(), standardParticipant);
    }

    private void saveSurveyAnswer(String answer, StandardParticipant standardParticipant) {
        StandardParticipantSurveyAnswer surveyAnswer = StandardParticipantSurveyAnswer.builder()
                .answerJson(answer)
                .standardParticipant(standardParticipant)
                .build();

        standardparticipantSurveyAnswerRepository.save(surveyAnswer);
    }
}
