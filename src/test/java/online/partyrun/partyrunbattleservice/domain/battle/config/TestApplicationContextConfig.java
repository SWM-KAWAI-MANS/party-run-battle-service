package online.partyrun.partyrunbattleservice.domain.battle.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.GenericApplicationContext;

@TestConfiguration
public class TestApplicationContextConfig {
    @Bean
    @Primary
    public GenericApplicationContext genericApplicationContext(
            final GenericApplicationContext gac) {
        return Mockito.spy(gac);
    }
}
