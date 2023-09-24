package online.partyrun.partyrunbattleservice.global.logging;

import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class RequestLogFormatter {

    private RequestLogFormatter() {
        throw new UnsupportedOperationException();
    }

    public static String toPrettyRequestString(ContentCachingRequestWrapper request) {
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

        return sb.toString();
    }
}
