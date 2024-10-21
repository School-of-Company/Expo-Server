package team.startup.expo.domain.sms.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.auth.exception.NotFoundSmsAuthException;
import team.startup.expo.domain.sms.SmsAuthEntity;
import team.startup.expo.domain.sms.exception.NotMatchRandomCodeException;
import team.startup.expo.domain.sms.presentation.dto.request.VerifySmsRequestDto;
import team.startup.expo.domain.sms.repository.SmsAuthRepository;
import team.startup.expo.domain.sms.service.VerifySmsService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class VerifySmsServiceImpl implements VerifySmsService {

    private final SmsAuthRepository smsAuthRepository;

    public void execute(VerifySmsRequestDto dto) {
        SmsAuthEntity smsAuthEntity = smsAuthRepository.findById(dto.getPhoneNumber())
                .orElseThrow(NotFoundSmsAuthException::new);

        if (Integer.parseInt(smsAuthEntity.getRandomValue()) != Integer.parseInt(dto.getRandomCode()))
            throw new NotMatchRandomCodeException();

        smsAuthEntity.changeAuthentication();

        smsAuthRepository.save(smsAuthEntity);
    }
}
