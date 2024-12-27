package team.startup.expo.domain.sms.service;

import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import team.startup.expo.domain.sms.presentation.dto.request.SendQrRequestDto;

public interface SendQrService {
    SingleMessageSentResponse execute(String expoId, SendQrRequestDto dto);
}
