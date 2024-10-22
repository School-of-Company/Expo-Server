package team.startup.expo.domain.admin.service;

import team.startup.expo.domain.admin.presentation.dto.response.GetPendingAdminResponseDto;

import java.util.List;

public interface GetPendingAdminsService {
    List<GetPendingAdminResponseDto> execute();
}
