package team.startup.expo.domain.admin.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.Admin;
import team.startup.expo.domain.admin.repository.AdminRepository;
import team.startup.expo.domain.admin.service.WithdrawalAdminService;
import team.startup.expo.domain.admin.util.UserUtil;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class WithdrawalAdminServiceImpl implements WithdrawalAdminService {

    private final AdminRepository adminRepository;
    private final UserUtil userUtil;

    public void execute() {
        Admin admin = userUtil.getCurrentUser();
        adminRepository.delete(admin);
    }
}
