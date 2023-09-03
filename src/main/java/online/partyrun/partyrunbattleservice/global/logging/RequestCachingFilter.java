package online.partyrun.partyrunbattleservice.global.logging;

import io.micrometer.core.instrument.util.IOUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@WebFilter(filterName = "RequestCachingFilter", urlPatterns = "/*")
public class RequestCachingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final CachedHttpServletRequest cachedRequest = new CachedHttpServletRequest(request);
        final String requestLogMessage = getRequestLogMessage(request, cachedRequest);
        logger.info(requestLogMessage);

        filterChain.doFilter(cachedRequest, response);
    }

    private String getRequestLogMessage(HttpServletRequest request, CachedHttpServletRequest cachedRequest) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n");
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

        sb.append("Request body: \n").append(IOUtils.toString(cachedRequest.getInputStream(), StandardCharsets.UTF_8)).append("\n");
        return sb.toString();
    }
}
