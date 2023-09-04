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
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

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
        final StringBuilder sb = new StringBuilder();
        sb.append("\nREQUEST\n");
        sb.append(request.getMethod()).append("  ").append(request.getRequestURL()).append("\n");
        sb.append("Headers: ").append("\n");

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            sb.append("  ").append(headerName).append(": ");
            Enumeration<String> headers = request.getHeaders(headerName);
            while (headers.hasMoreElements()) {
                String headerValue = headers.nextElement();
                sb.append(headerValue);
                if (headers.hasMoreElements()) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }

        sb.append("Request body: \n").append(new String(request.getContentAsByteArray(), StandardCharsets.UTF_8)).append("\n");
        logger.info(sb.toString());
    }

    private void logResponseMessage(ContentCachingRequestWrapper request, HttpServletResponse response, long duration) {
        String sb = "\nRESPONSE\n" + response.getStatus() + " " + request.getMethod() + " " + request.getRequestURL() +
                " duration: " + duration + "ms\n";

        logger.info(sb);
    }
}
