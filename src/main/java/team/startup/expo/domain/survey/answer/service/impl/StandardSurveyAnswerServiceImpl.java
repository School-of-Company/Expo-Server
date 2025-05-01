package team.startup.expo.domain.survey.answer.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.sms.exception.NotFoundParticipantException;
import team.startup.expo.domain.survey.answer.entity.Draw;
import team.startup.expo.domain.survey.answer.entity.StandardParticipantSurveyAnswer;
import team.startup.expo.domain.survey.answer.event.SendDrawResultEvent;
import team.startup.expo.domain.survey.answer.exception.AlreadyExistSurveyAnswerException;
import team.startup.expo.domain.survey.answer.presentation.dto.request.SurveyAnswerRequestDto;
import team.startup.expo.domain.survey.answer.repository.StandardParticipantSurveyAnswerRepository;
import team.startup.expo.domain.survey.answer.service.StandardSurveyAnswerService;
import team.startup.expo.domain.survey.management.entity.Survey;
import team.startup.expo.domain.survey.management.exception.NotFoundSurveyException;
import team.startup.expo.domain.survey.management.repository.SurveyRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class StandardSurveyAnswerServiceImpl implements StandardSurveyAnswerService {

    private final StandardParticipantSurveyAnswerRepository standardparticipantSurveyAnswerRepository;
    private final StandardParticipantRepository participantRepository;
    private final ExpoRepository expoRepository;
    private final SurveyRepository surveyRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void execute(String expoId, SurveyAnswerRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        StandardParticipant standardParticipant = participantRepository.findByPhoneNumberAndExpo(dto.getPhoneNumber(), expo)
                .orElseThrow(NotFoundParticipantException::new);

        if (standardparticipantSurveyAnswerRepository.existsByStandardParticipant(standardParticipant))
            throw new AlreadyExistSurveyAnswerException();

        saveSurveyAnswer(dto, standardParticipant);

        processDraw(expo, standardParticipant);
    }

    private void saveSurveyAnswer(
        SurveyAnswerRequestDto dto,
        StandardParticipant standardParticipant
    ) {
        StandardParticipantSurveyAnswer standardSurveyAnswer =
                standardparticipantSurveyAnswerRepository.findByStandardParticipantForWrite(standardParticipant);

        if (standardSurveyAnswer != null)
            throw new AlreadyExistSurveyAnswerException();

        StandardParticipantSurveyAnswer surveyAnswer = StandardParticipantSurveyAnswer.builder()
                .answerJson(dto.getAnswerJson())
                .standardParticipant(standardParticipant)
                .personalInformationStatus(dto.getPersonalInformationStatus())
                .build();

        standardparticipantSurveyAnswerRepository.save(surveyAnswer);

    }

    private void processDraw(Expo expo, StandardParticipant standardParticipant) {
        Survey survey = surveyRepository.findByExpoAndParticipationType(expo, ParticipationType.STANDARD)
                .orElseThrow(NotFoundSurveyException::new);

        survey.plusTotalAnswer();

        surveyRepository.save(survey);

        int nowNumber = survey.getTotalAnswers();

        for (Draw draw : Draw.values()) {
            if (draw.getNumber().equals(nowNumber)) {
                applicationEventPublisher.publishEvent(
                        new SendDrawResultEvent(standardParticipant.getPhoneNumber(),draw.getNumber()));
            }
        }
    }
}
