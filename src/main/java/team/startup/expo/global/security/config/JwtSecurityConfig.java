package team.startup.expo.global.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import team.startup.expo.global.filter.JwtFilter;
import team.startup.expo.global.security.jwt.JwtProvider;
import team.startup.expo.global.security.jwt.TokenParser;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final JwtProvider jwtProvider;
    private final TokenParser tokenParser;

    @Override
    public void configure(HttpSecurity http) {
        JwtFilter customFilter = new JwtFilter(jwtProvider, tokenParser);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
