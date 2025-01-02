package team.startup.expo.domain.sms.service;

public interface VerifySmsService {
    void execute(String phoneNumber, String code);
}
