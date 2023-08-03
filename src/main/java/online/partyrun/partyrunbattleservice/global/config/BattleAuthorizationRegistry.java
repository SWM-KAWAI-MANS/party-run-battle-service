package online.partyrun.partyrunbattleservice.global.config;

import online.partyrun.springsecurityauthorizationmanager.AuthorizationRegistry;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class BattleAuthorizationRegistry implements AuthorizationRegistry {

    @Override
    public AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
            match(
                    AuthorizeHttpRequestsConfigurer<HttpSecurity>
                                    .AuthorizationManagerRequestMatcherRegistry
                            r) {
        return r.requestMatchers("/docs/index.html")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/battles")
                .hasRole("SYSTEM")
                .anyRequest()
                .hasRole("USER");
    }
}
