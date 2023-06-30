package online.partyrun.partyrunbattleservice.global.config;

import lombok.extern.slf4j.Slf4j;

import online.partyrun.springsecurityauthorizationmanager.AuthorizationRegistry;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BattleAuthorizationRegistry implements AuthorizationRegistry {

    @Override
    public AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
            match(
                    AuthorizeHttpRequestsConfigurer<HttpSecurity>
                                    .AuthorizationManagerRequestMatcherRegistry
                            r) {
        return r.requestMatchers("/battle").permitAll().anyRequest().hasRole("USER");
    }
}
