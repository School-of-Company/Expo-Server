package team.startup.expo.domain.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import team.startup.expo.domain.admin.entity.Admin;
import team.startup.expo.domain.admin.entity.Status;
import team.startup.expo.domain.admin.repository.AdminRepository;
import team.startup.expo.domain.auth.exception.DuplicateEmailException;
import team.startup.expo.domain.auth.exception.DuplicateNicknameException;
import team.startup.expo.domain.auth.exception.DuplicatePhoneNumberException;
import team.startup.expo.domain.auth.exception.NotFoundSmsAuthException;
import team.startup.expo.domain.auth.presentation.dto.request.SignUpRequestDto;
import team.startup.expo.domain.auth.service.SignUpService;
import team.startup.expo.domain.sms.entity.SmsAuthEntity;
import team.startup.expo.domain.sms.repository.SmsAuthRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsAuthRepository smsAuthRepository;

    public void execute(SignUpRequestDto dto) {
        if (adminRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            smsAuthRepository.deleteById(dto.getPhoneNumber());
            throw new DuplicatePhoneNumberException();
        }

        if (adminRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateEmailException();

        SmsAuthEntity smsAuthEntity = smsAuthRepository.findById(dto.getPhoneNumber())
                .orElseThrow(NotFoundSmsAuthException::new);

        if (!smsAuthEntity.getAuthentication())
            throw new NotFoundSmsAuthException();

        if (adminRepository.existsByNickname(dto.getNickname()))
            throw new DuplicateNicknameException();

        smsAuthRepository.delete(smsAuthEntity);

        saveAdmin(dto);
    }

    private void saveAdmin(SignUpRequestDto dto) {
        Admin admin = Admin.builder()
                .name(dto.getName())
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phoneNumber(dto.getPhoneNumber())
                .status(Status.PENDING)
                .build();

        adminRepository.save(admin);
    }
}
