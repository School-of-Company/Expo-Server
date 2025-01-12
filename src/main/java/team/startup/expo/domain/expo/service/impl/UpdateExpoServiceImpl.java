package team.startup.expo.domain.expo.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.entity.Admin;
import team.startup.expo.domain.admin.util.UserUtil;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.exception.NotMatchAdminException;
import team.startup.expo.domain.expo.presentation.dto.request.UpdateExpoRequestDto;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.expo.service.UpdateExpoService;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class UpdateExpoServiceImpl implements UpdateExpoService {

    private final ExpoRepository expoRepository;
    private final UserUtil userUtil;

    public void execute(String expoId, UpdateExpoRequestDto dto) {
        Admin admin = userUtil.getCurrentUser();

        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (expo.getAdmin() != admin)
            throw new NotMatchAdminException();

        expoRepository.save(dto.toEntity(expo));
    }
}
