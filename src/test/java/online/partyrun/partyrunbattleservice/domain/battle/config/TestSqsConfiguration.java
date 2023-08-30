package online.partyrun.partyrunbattleservice.domain.battle.config;

import online.partyrun.partyrunbattleservice.domain.battle.event.SqsMessageListener;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestSqsConfiguration {
    @MockBean
    public SqsMessageListener messageListener;
}
