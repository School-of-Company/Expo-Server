package team.startup.expo.domain.admin.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.admin.presentation.dto.response.GetPendingAdminResponseDto;
import team.startup.expo.domain.admin.service.AcceptAdminService;
import team.startup.expo.domain.admin.service.GetPendingAdminsService;
import team.startup.expo.domain.admin.service.WithdrawalAdminService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AcceptAdminService acceptAdminService;
    private final GetPendingAdminsService getPendingAdminsService;
    private final WithdrawalAdminService withdrawalAdminService;

    @PatchMapping("/{admin_id}")
    public ResponseEntity<Void> acceptAdmin(@PathVariable("admin_id") Long adminId) {
        acceptAdminService.execute(adminId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<GetPendingAdminResponseDto>> getPendingAdmins() {
        List<GetPendingAdminResponseDto> responseDtos = getPendingAdminsService.execute();
        return ResponseEntity.ok(responseDtos);
    }

    @DeleteMapping
    public ResponseEntity<Void> withdrawalAdmin() {
        withdrawalAdminService.execute();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
