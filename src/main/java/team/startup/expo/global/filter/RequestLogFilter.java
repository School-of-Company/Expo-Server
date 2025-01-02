package team.startup.expo.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.filter.OncePerRequestFilter;
import team.startup.expo.global.filter.event.ErrorLoggingEvent;

@Slf4j
@AllArgsConstructor
public class RequestLogFilter extends OncePerRequestFilter {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        log.info("=========================");

        String[] logMessages = {
                "client ip = " + request.getRemoteAddr(),
                "request method = " + request.getMethod(),
                "request url = " + request.getRequestURI(),
                "client info = " + request.getHeader("User-Agent")
        };

        for (String message : logMessages) {
            log.info(message);
        }

        log.info("=========================");

        try {
            filterChain.doFilter(request, response);
            log.info("=========================");
            log.info("response status = " + response.getStatus());
            log.info("=========================");
        } catch (Exception e) {
            log.error("=========================");
            log.error(e.getCause().toString());
            log.error(e.getMessage());
            log.error(e.getCause().getMessage());
            log.error("=========================");

            applicationEventPublisher.publishEvent(new ErrorLoggingEvent(response.getStatus(), e.getCause().toString()));
        }
    }
}


