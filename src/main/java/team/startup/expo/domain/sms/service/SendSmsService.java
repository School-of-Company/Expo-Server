package team.startup.expo.domain.sms.service;

import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import team.startup.expo.domain.sms.presentation.dto.request.SendSmsRequestDto;

public interface SendSmsService {
    SingleMessageSentResponse execute(SendSmsRequestDto dto);
}
