package team.startup.expo.domain.sms.service.impl;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import team.startup.expo.domain.sms.entity.SmsAuthEntity;
import team.startup.expo.domain.sms.presentation.dto.request.SendSmsRequestDto;
import team.startup.expo.domain.sms.repository.SmsAuthRepository;
import team.startup.expo.domain.sms.service.SendSmsService;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.sms.SmsProperties;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@TransactionService
@RequiredArgsConstructor
public class SendSmsServiceImpl implements SendSmsService {

    private final DefaultMessageService messageService;
    private final SmsProperties smsProperties;
    private final SmsAuthRepository smsAuthRepository;

    public SingleMessageSentResponse execute(SendSmsRequestDto dto) {
        Message message = new Message();
        String randomCode = createCode();

        message.setFrom(smsProperties.getFromNumber());
        message.setTo(dto.getPhoneNumber());
        message.setText(randomCode);

        saveSmsAuth(dto, randomCode);

        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));

        return response;
    }

    private String createCode() {
        int length = 4;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    private void saveSmsAuth(SendSmsRequestDto dto, String randomCode) {
        SmsAuthEntity smsAuthEntity = smsAuthRepository.findById(dto.getPhoneNumber())
                .orElse(SmsAuthEntity.builder()
                        .phone(dto.getPhoneNumber())
                        .randomValue(randomCode)
                        .attemptCount(0)
                        .authentication(false)
                        .build());

        smsAuthEntity.plusAttemptCount();
        smsAuthRepository.save(smsAuthEntity);
    }
}
