package team.startup.expo.domain.admin.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.entity.Status;
import team.startup.expo.domain.admin.presentation.dto.response.GetPendingAdminResponseDto;
import team.startup.expo.domain.admin.repository.AdminRepository;
import team.startup.expo.domain.admin.service.GetPendingAdminsService;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;
import java.util.stream.Collectors;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetPendingAdminsServiceImpl implements GetPendingAdminsService {

    private final AdminRepository adminRepository;

    public List<GetPendingAdminResponseDto> execute() {
        return adminRepository.findByStatus(Status.PENDING).stream()
                .map(admin -> GetPendingAdminResponseDto.builder()
                        .id(admin.getId())
                        .name(admin.getName())
                        .nickname(admin.getNickname())
                        .email(admin.getEmail())
                        .phoneNumber(admin.getPhoneNumber())
                        .build())
                .collect(Collectors.toList());


    }
}
