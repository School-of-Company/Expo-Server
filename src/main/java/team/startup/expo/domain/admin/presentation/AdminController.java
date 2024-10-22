package team.startup.expo.domain.admin.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.expo.domain.admin.service.AcceptAdminService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AcceptAdminService acceptAdminService;

    @PatchMapping("/{admin_id}")
    public ResponseEntity<Void> acceptAdmin(@PathVariable("admin_id") Long adminId) {
        acceptAdminService.execute(adminId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
