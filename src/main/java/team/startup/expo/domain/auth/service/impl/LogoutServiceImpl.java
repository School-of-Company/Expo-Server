package team.startup.expo.domain.auth.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.Admin;
import team.startup.expo.domain.admin.util.UserUtil;
import team.startup.expo.domain.auth.repository.RefreshTokenRepository;
import team.startup.expo.domain.auth.service.LogoutService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserUtil userUtil;

    public void execute() {
        Admin admin = userUtil.getCurrentUser();
        refreshTokenRepository.deleteById(admin.getEmail());
    }
}
