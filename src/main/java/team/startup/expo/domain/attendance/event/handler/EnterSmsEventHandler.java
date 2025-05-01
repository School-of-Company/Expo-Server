package team.startup.expo.domain.attendance.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.attendance.event.EnterSmsEvent;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.sms.exception.NotFoundParticipantException;
import team.startup.expo.domain.sms.exception.NotFoundTraineeException;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;
import team.startup.expo.global.sms.SmsProperties;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnterSmsEventHandler {

    private final ExpoRepository expoRepository;
    private final DefaultMessageService messageService;
    private final SmsProperties smsProperties;
    private final StandardParticipantRepository standardParticipantRepository;
    private final TraineeRepository traineeRepository;

    @Async("asyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public CompletableFuture<SingleMessageSentResponse> sendLeaveSmsHandler(EnterSmsEvent event) {
        SingleMessageSentResponse response = null;

        try {
            Expo expo = expoRepository.findById(event.getExpoId())
                    .orElseThrow(NotFoundExpoException::new);

            if (event.getAuthority() == Authority.ROLE_STANDARD) {
                StandardParticipant participant = standardParticipantRepository.findByPhoneNumberAndExpo(event.getPhoneNumber(), expo)
                        .orElseThrow(NotFoundParticipantException::new);

                String information = "2025 AI•SW체험축전 설문조사 \n 2025 광주AI•SW체험축전을 방문해주셔서 감사합니다. " +
                        "체험 후 꼭 설문에 응답해주세요. ==> https://startup-expo.kr/application/" + expo.getId()
                        + "?formType=survey&userType=STANDARD&applicationType=register&name=" + participant.getName() + "&phoneNumber=" + participant.getPhoneNumber();

                Message message = createMessage(event, information);

                response = messageService.sendOne(new SingleMessageSendingRequest(message));
            } else if (event.getAuthority() == Authority.ROLE_TRAINEE) {
                Trainee trainee = traineeRepository.findByPhoneNumberAndExpo(event.getPhoneNumber(), expo)
                        .orElseThrow(NotFoundTraineeException::new);

                String information = "박람회 퇴장 문자입니다. \n 박람회 만족도 조사에 참가해주세요. \n https://startup-expo.kr/application/" + expo.getId()
                        + "?formType=survey&userType=TRAINEE&applicationType=register&name=" + trainee.getName() + "&phoneNumber=" + trainee.getPhoneNumber();

                Message message = createMessage(event, information);

                response = messageService.sendOne(new SingleMessageSendingRequest(message));
            }
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return CompletableFuture.completedFuture(response);
    }

    private Message createMessage(EnterSmsEvent event, String information) {
        try {
            Message message = new Message();
            message.setFrom(smsProperties.getFromNumber());
            message.setTo(event.getPhoneNumber());
            message.setText(information);

            return message;
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
