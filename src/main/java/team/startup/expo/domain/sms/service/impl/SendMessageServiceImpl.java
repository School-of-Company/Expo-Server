package team.startup.expo.domain.sms.service.impl;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotExistParticipantAtExpoException;
import team.startup.expo.domain.expo.exception.NotExistTraineeAtExpoException;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.ParticipantRepository;
import team.startup.expo.domain.sms.presentation.dto.request.SendMessageRequestDto;
import team.startup.expo.domain.sms.service.SendMessageService;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;
import team.startup.expo.global.sms.SmsProperties;

import java.util.ArrayList;
import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class SendMessageServiceImpl implements SendMessageService {

    private final DefaultMessageService messageService;
    private final SmsProperties smsProperties;
    private final TraineeRepository traineeRepository;
    private final ParticipantRepository participantRepository;
    private final ExpoRepository expoRepository;

    public MultipleDetailMessageSentResponse execute(String expoId, SendMessageRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        MultipleDetailMessageSentResponse response = new MultipleDetailMessageSentResponse();

        switch (dto.getAuthority()) {
            case ROLE_STANDARD -> response = sendMessageForParticipant(expo, dto);
            case ROLE_TRAINEE -> response = sendMessageForTrainee(expo, dto);
        }

        return response;
    }

    private MultipleDetailMessageSentResponse sendMessageForTrainee(Expo expo, SendMessageRequestDto dto) {
        List<Trainee> traineeList = traineeRepository.findByExpo(expo);

        if (traineeList.isEmpty())
            throw new NotExistTraineeAtExpoException();

        ArrayList<Message> messageList = new ArrayList<>();

        for (Trainee trainee : traineeList) {
            Message message = new Message();
            message.setFrom(smsProperties.getFromNumber());
            message.setTo(trainee.getPhoneNumber());
            message.setText(dto.getTitle() + "\n" + dto.getContent());

            messageList.add(message);
        }

        return sendMessage(messageList);
    }

    private MultipleDetailMessageSentResponse sendMessageForParticipant(Expo expo, SendMessageRequestDto dto) {
        List<StandardParticipant> participantList = participantRepository.findByExpo(expo);

        if (participantList.isEmpty())
            throw new NotExistParticipantAtExpoException();

        ArrayList<Message> messageList = new ArrayList<>();

        for (StandardParticipant participant : participantList) {
            Message message = new Message();
            message.setFrom(smsProperties.getFromNumber());
            message.setTo(participant.getPhoneNumber());
            message.setText(dto.getTitle() + "\n" + dto.getContent());

            messageList.add(message);
        }

        return sendMessage(messageList);
    }

    private MultipleDetailMessageSentResponse sendMessage(ArrayList<Message> messageList) {
        MultipleDetailMessageSentResponse response = new MultipleDetailMessageSentResponse();

        try {
            response = messageService.send(messageList, false, true);
        } catch (NurigoMessageNotReceivedException exception) {
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

        return response;
    }
}
