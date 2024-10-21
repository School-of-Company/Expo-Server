package team.startup.expo.domain.sms.presentation;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.sms.presentation.dto.request.SendSmsRequestDto;
import team.startup.expo.domain.sms.presentation.dto.request.VerifySmsRequestDto;
import team.startup.expo.domain.sms.service.SendSmsService;
import team.startup.expo.domain.sms.service.VerifySmsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final SendSmsService sendSmsService;
    private final VerifySmsService verifySmsService;

    @PostMapping
    public ResponseEntity<SingleMessageSentResponse> sendSms(@RequestBody SendSmsRequestDto dto) {
        SingleMessageSentResponse response = sendSmsService.execute(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Void> verifySms(@RequestBody VerifySmsRequestDto dto) {
        verifySmsService.execute(dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
