package team.startup.expo.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import team.startup.expo.domain.admin.exception.NotFoundUserException;
import team.startup.expo.domain.admin.repository.AdminRepository;

@Service
@RequiredArgsConstructor
public class AuthDetailsService implements UserDetailsService {
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return adminRepository.findByEmail(email)
                .map(AuthDetails::new)
                .orElseThrow(NotFoundUserException::new);
    }
}
