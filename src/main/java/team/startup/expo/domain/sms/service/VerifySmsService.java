package team.startup.expo.domain.sms.service;

import team.startup.expo.domain.sms.presentation.dto.request.VerifySmsRequestDto;

public interface VerifySmsService {
    void execute(String phoneNumber, String code);
}
