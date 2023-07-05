package online.partyrun.partyrunbattleservice.domain.battle.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@TestConfiguration
public class TestTimeConfig {

    @Bean
    @Primary
    public Clock testClock() {
        final ZoneOffset zoneOffset = ZoneOffset.UTC;
        return Clock.fixed(LocalDateTime.of(2024,1,1,1,1,1,1).atOffset(zoneOffset).toInstant(), zoneOffset);
    }
}
