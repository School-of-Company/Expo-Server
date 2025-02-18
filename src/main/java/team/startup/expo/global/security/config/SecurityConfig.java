package team.startup.expo.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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

import team.startup.expo.domain.admin.entity.Authority;
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
    private final ApplicationEventPublisher applicationEventPublisher;

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

                                // actuator
                                .requestMatchers(HttpMethod.GET, "/actuator/prometheus").permitAll()

                                // health
                                .requestMatchers(HttpMethod.GET, "/health").permitAll()

                                // sms
                                .requestMatchers(HttpMethod.POST, "/sms").permitAll()
                                .requestMatchers(HttpMethod.GET, "/sms").permitAll()
                                .requestMatchers(HttpMethod.POST, "/sms/qr/{expo_id}").permitAll()
                                .requestMatchers(HttpMethod.POST, "/sms/message/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())

                                // admin
                                .requestMatchers(HttpMethod.PATCH, "/admin/{admin_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/admin").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/admin").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/admin/{admin_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/admin/my").hasAnyAuthority(Authority.ROLE_ADMIN.name())

                                // excel
                                .requestMatchers(HttpMethod.GET, "/excel/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/excel/standard/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())

                                // auth
                                .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/signin").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/auth").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/auth").authenticated()

                                // trainee
                                .requestMatchers(HttpMethod.GET, "/trainee/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())

                                // participant
                                .requestMatchers(HttpMethod.GET, "/participant/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())

                                // training
                                .requestMatchers(HttpMethod.GET, "/training/{trainingPro_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.POST, "/training/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.PATCH, "/training/{trainingPro_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/training/{trainingPro_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/training/program/{expo_id}").permitAll()
                                .requestMatchers(HttpMethod.POST, "/training/list/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.POST, "/training/application/{trainingPro_id}").permitAll()
                                .requestMatchers(HttpMethod.POST, "/training/application/list").permitAll()

                                // standard
                                .requestMatchers(HttpMethod.POST, "/standard/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.POST, "/standard/list/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.PATCH, "/standard/{standardPro_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/standard/{standardPro_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/standard/program/{expo_id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/standard/{standardPro_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.POST, "standard/application/{expo_id}").permitAll()

                                // expo
                                .requestMatchers(HttpMethod.POST, "/expo").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.PATCH, "/expo/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/expo/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/expo/{expo_id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/expo").permitAll()

                                // attendance
                                .requestMatchers(HttpMethod.PATCH, "/attendance/training/{trainingPro_id}").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/attendance/standard/{standardPro_id}").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/attendance/{expo_id}").permitAll()

                                // application
                                .requestMatchers(HttpMethod.POST, "/application/{expo_id}").permitAll()
                                .requestMatchers(HttpMethod.POST, "/application/pre-standard/{expo_id}").permitAll()
                                .requestMatchers(HttpMethod.POST, "/application/field/{expo_id}").permitAll()
                                .requestMatchers(HttpMethod.POST, "/application/field/standard/{expo_id}").permitAll()

                                // form
                                .requestMatchers(HttpMethod.POST, "/form/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.PATCH, "/form/{form_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/form/{form_id}/{participationType}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/form/{expo_id}").permitAll()

                                // survey
                                .requestMatchers(HttpMethod.POST, "/survey/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/survey/{expo_id}").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/survey/{expo_id}").hasAnyAuthority(Authority.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/survey/{expo_id}/{participationType}").hasAnyAuthority(Authority.ROLE_ADMIN.name())

                                // surveyAnswer
                                .requestMatchers(HttpMethod.POST, "/survey/answer/standard/{expo_id}").permitAll()
                                .requestMatchers(HttpMethod.POST, "/survey/answer/trainee/{expo_id}").permitAll()

                                //image
                                .requestMatchers(HttpMethod.POST, "/image").authenticated()

                                .anyRequest().denyAll()
                )


                .addFilterBefore(new JwtFilter(jwtProvider, tokenParser), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ExceptionFilter(objectMapper, applicationEventPublisher), JwtFilter.class)
                .addFilterBefore(new RequestLogFilter(applicationEventPublisher), ExceptionFilter.class);

        return http.build();

    }
}
