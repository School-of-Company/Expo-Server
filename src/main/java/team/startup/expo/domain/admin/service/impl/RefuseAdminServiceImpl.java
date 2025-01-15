package team.startup.expo.domain.admin.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.entity.Admin;
import team.startup.expo.domain.admin.entity.Status;
import team.startup.expo.domain.admin.exception.AlreadyAcceptUserException;
import team.startup.expo.domain.admin.exception.NotFoundUserException;
import team.startup.expo.domain.admin.repository.AdminRepository;
import team.startup.expo.domain.admin.service.RefuseAdminService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class RefuseAdminServiceImpl implements RefuseAdminService {

    private final AdminRepository adminRepository;

    public void execute(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(NotFoundUserException::new);

        if (admin.getStatus() != Status.PENDING)
            throw new AlreadyAcceptUserException();

        adminRepository.delete(admin);
    }
}
