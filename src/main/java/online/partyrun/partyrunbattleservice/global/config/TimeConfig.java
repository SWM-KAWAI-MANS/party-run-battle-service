package online.partyrun.partyrunbattleservice.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class TimeConfig {

    @Bean
    public SystemTime systemTime() {
        return new SystemTime(LocalDateTime.now());
    }
}
