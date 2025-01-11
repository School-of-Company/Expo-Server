package team.startup.expo.domain.admin.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import team.startup.expo.domain.admin.entity.Admin;
import team.startup.expo.domain.admin.exception.NotFoundUserException;
import team.startup.expo.domain.admin.repository.AdminRepository;

@Component
@RequiredArgsConstructor
public class UserUtil {

    private final AdminRepository adminRepository;

    public Admin getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return adminRepository.findByEmail(email)
                .orElseThrow(NotFoundUserException::new);
    }
}
