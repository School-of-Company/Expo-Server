package team.startup.expo.domain.admin.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.entity.Admin;
import team.startup.expo.domain.admin.presentation.dto.response.GetMyInformationResponseDto;
import team.startup.expo.domain.admin.service.GetMyInformationService;
import team.startup.expo.domain.admin.util.UserUtil;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetMyInformationServiceImpl implements GetMyInformationService {

    private final UserUtil userUtil;

    public GetMyInformationResponseDto execute() {
        Admin admin = userUtil.getCurrentUser();

        return GetMyInformationResponseDto.builder()
                .name(admin.getName())
                .nickname(admin.getNickname())
                .email(admin.getEmail())
                .build();
    }
}
