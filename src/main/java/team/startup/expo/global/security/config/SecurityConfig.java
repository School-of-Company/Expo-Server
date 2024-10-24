package team.startup.expo.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import team.startup.expo.domain.admin.Authority;
import team.startup.expo.global.filter.ExceptionFilter;
import team.startup.expo.global.filter.JwtFilter;
import team.startup.expo.global.filter.RequestLogFilter;
import team.startup.expo.global.security.handler.JwtAccessDeniedHandler;
import team.startup.expo.global.security.handler.JwtAuthenticationEntryPoint;
import team.startup.expo.global.security.jwt.JwtProvider;
import team.startup.expo.global.security.jwt.TokenParser;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final ObjectMapper objectMapper;
    private final TokenParser tokenParser;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)

                .exceptionHandling(exceptionConfig ->
                        exceptionConfig.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

                                // sms
                                .requestMatchers(HttpMethod.POST, "/sms").permitAll()
                                .requestMatchers(HttpMethod.GET, "/sms").permitAll()

                                // admin
                                .requestMatchers(HttpMethod.PATCH, "/admin/{admin_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/admin").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/admin").hasAnyAuthority(Authority.ROLE_ADMIN.name())

                                // auth
                                .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/signin").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/auth").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/auth").authenticated()

                                // trainee
                                .requestMatchers(HttpMethod.GET, "/trainee").hasAnyAuthority(Authority.ROLE_ADMIN.name())

                                // training
                                .requestMatchers(HttpMethod.GET, "/training/{trainingPro_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())

                                // expo
                                .requestMatchers(HttpMethod.POST, "/expo").hasAnyAuthority(Authority.ROLE_ADMIN.name())

                                .anyRequest().denyAll()
                )

                .addFilterBefore(new RequestLogFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ExceptionFilter(objectMapper), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(jwtProvider, tokenParser), UsernamePasswordAuthenticationFilter.class);


        return http.build();

    }
}
