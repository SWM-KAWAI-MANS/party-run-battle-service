package online.partyrun.partyrunbattleservice.domain.runner.entity.record;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GpsData {
    Location location;
    LocalDateTime time;

    public static GpsData of(double longitude, double latitude, double altitude, LocalDateTime time) {
        return new GpsData(Location.of(longitude, latitude, altitude), time);
    }
}
