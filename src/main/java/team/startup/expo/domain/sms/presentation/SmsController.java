package team.startup.expo.domain.sms.presentation;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.sms.presentation.dto.request.SendQrRequestDto;
import team.startup.expo.domain.sms.presentation.dto.request.SendSmsRequestDto;
import team.startup.expo.domain.sms.service.SendQrService;
import team.startup.expo.domain.sms.service.SendSmsService;
import team.startup.expo.domain.sms.service.VerifySmsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final SendSmsService sendSmsService;
    private final VerifySmsService verifySmsService;
    private final SendQrService sendQrService;

    @PostMapping
    public ResponseEntity<SingleMessageSentResponse> sendSms(@RequestBody SendSmsRequestDto dto) {
        SingleMessageSentResponse response = sendSmsService.execute(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Void> verifySms(@RequestParam String phoneNumber, @RequestParam String code) {
        verifySmsService.execute(phoneNumber, code);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/qr")
    public ResponseEntity<SingleMessageSentResponse> sendQr(@RequestBody SendQrRequestDto dto) {
        SingleMessageSentResponse response = sendQrService.execute(dto);
        return ResponseEntity.ok(response);
    }
}
