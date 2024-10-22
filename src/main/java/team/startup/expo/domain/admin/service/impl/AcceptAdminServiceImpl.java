package team.startup.expo.domain.admin.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.Admin;
import team.startup.expo.domain.admin.Authority;
import team.startup.expo.domain.admin.Status;
import team.startup.expo.domain.admin.exception.NotFoundUserException;
import team.startup.expo.domain.admin.repository.AdminRepository;
import team.startup.expo.domain.admin.service.AcceptAdminService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class AcceptAdminServiceImpl implements AcceptAdminService {

    private final AdminRepository adminRepository;

    public void execute(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(NotFoundUserException::new);

        saveAdmin(admin);
    }

    private void saveAdmin(Admin admin) {
        Admin acceptAdmin = Admin.builder()
                .id(admin.getId())
                .name(admin.getName())
                .nickname(admin.getNickname())
                .email(admin.getEmail())
                .password(admin.getPassword())
                .phoneNumber(admin.getPhoneNumber())
                .status(Status.ACCEPTED)
                .authority(Authority.ROLE_ADMIN)
                .build();

        adminRepository.save(acceptAdmin);
    }
}
