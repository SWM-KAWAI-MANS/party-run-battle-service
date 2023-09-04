package online.partyrun.partyrunbattleservice.global.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);

        long startTime = System.currentTimeMillis();
        filterChain.doFilter(cachingRequest, response);
        long endTime = System.currentTimeMillis();

        logRequestMessage(cachingRequest);
        logResponseMessage(cachingRequest, response, endTime - startTime);
    }

    private void logRequestMessage(ContentCachingRequestWrapper request) {
        logger.info(RequestLogFormatter.toPrettyRequestString(request));
    }

    private void logResponseMessage(ContentCachingRequestWrapper request, HttpServletResponse response, long duration) {
        String sb = "\nRESPONSE\n" + response.getStatus() + " " + request.getMethod() + " " + request.getRequestURL() +
                " duration: " + duration + "ms\n";

        logger.info(sb);
    }
}
