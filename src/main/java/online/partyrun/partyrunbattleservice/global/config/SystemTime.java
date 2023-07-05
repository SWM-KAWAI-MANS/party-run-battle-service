package online.partyrun.partyrunbattleservice.global.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemTime {

    ZoneOffset zoneOffset = ZoneOffset.UTC;
    Clock clock;

    public SystemTime(LocalDateTime dateTime) {
        this.clock = Clock.fixed(dateTime.atOffset(zoneOffset).toInstant(), zoneOffset);
    }

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}
