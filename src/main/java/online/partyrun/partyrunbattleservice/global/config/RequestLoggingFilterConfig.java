package online.partyrun.partyrunbattleservice.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingFilterConfig {

    @Bean
    public AbstractRequestLoggingFilter logFilter() {

        final CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();

        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(true);
        filter.setMaxPayloadLength(10000);
        filter.setBeforeMessagePrefix("BEFORE REQUEST : ");
        filter.setAfterMessagePrefix("AFTER REQUEST : ");
        return filter;
    }
}
