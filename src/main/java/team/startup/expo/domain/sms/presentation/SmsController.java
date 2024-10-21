package team.startup.expo.domain.sms.presentation;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.startup.expo.domain.sms.presentation.dto.request.SendSmsRequestDto;
import team.startup.expo.domain.sms.service.SendSmsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final SendSmsService sendSmsService;

    @PostMapping
    public ResponseEntity<SingleMessageSentResponse> sendSms(@RequestBody SendSmsRequestDto dto) {
        SingleMessageSentResponse response = sendSmsService.execute(dto);
        return ResponseEntity.ok(response);
    }


}
