package team.startup.expo.domain.survey.answer.event.handler;

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
import team.startup.expo.domain.survey.answer.event.SendDrawResultEvent;
import team.startup.expo.global.sms.SmsProperties;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendDrawResultEventHandler {

    private final SmsProperties smsProperties;
    private final DefaultMessageService messageService;

    @Async("asyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public CompletableFuture<SingleMessageSentResponse> sendDrawResultEventHandler(SendDrawResultEvent event) {
        Message message = new Message();
        message.setFrom(smsProperties.getFromNumber());
        message.setTo(event.getPhoneNumber());
        message.setText("축 당첨! 설문조사 행운의 숫자에 당첨되셨습니다. 선물은 입구 운영본부에서 받아가세요!\n" +
                event.getDrawNumber() + "번!");

        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
        return CompletableFuture.completedFuture(response);
    }

}
