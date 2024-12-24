package team.startup.expo.domain.sms.service;

import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import team.startup.expo.domain.sms.presentation.dto.request.SendMessageRequestDto;

public interface SendMessageService {
    MultipleDetailMessageSentResponse execute(String expoId, SendMessageRequestDto dto);
}
