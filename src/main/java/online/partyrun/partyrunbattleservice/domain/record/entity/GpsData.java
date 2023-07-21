package online.partyrun.partyrunbattleservice.domain.record.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.record.exception.InvalidGpsDataTimeException;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GpsData implements Comparable<GpsData> {
    Location location;
    LocalDateTime time;

    public static GpsData of(double longitude, double latitude, double altitude, LocalDateTime time, LocalDateTime minTime) {
        validateMinTime(time, minTime);

        return new GpsData(Location.of(longitude, latitude, altitude), time);
    }

    private static void validateMinTime(LocalDateTime time, LocalDateTime minTime) {
        if (time.isBefore(minTime)) {
            throw new InvalidGpsDataTimeException(time, minTime);
        }
    }

    @Override
    public int compareTo(GpsData o) {
        return this.time.compareTo(o.time);
    }

    public double calculateDistance(GpsData gpsData) {
        return this.location.calculateDistance(gpsData.location);
    }
}
