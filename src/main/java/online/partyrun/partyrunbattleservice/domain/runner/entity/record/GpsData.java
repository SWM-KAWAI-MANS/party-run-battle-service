package online.partyrun.partyrunbattleservice.domain.runner.entity.record;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.runner.exception.GpsTimeNullException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidGpsDataException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.PastGpsDataTimeException;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GpsData implements Comparable<GpsData> {
    Location location;
    LocalDateTime time;

    public static GpsData of(
            double longitude, double latitude, double altitude, LocalDateTime time) {
        validateTime(time);
        return new GpsData(Location.of(longitude, latitude, altitude), time);
    }

    private static void validateTime(LocalDateTime time) {
        if (Objects.isNull(time)) {
            throw new GpsTimeNullException();
        }
    }

    public boolean isBefore(LocalDateTime time) {
        return this.time.isBefore(time);
    }

    public double calculateDistance(GpsData other) {
        validateNull(other);
        validateIsBeforeData(other.time);
        return this.location.calculateDistance(other.location, new HaversineDistanceCalculator());
    }

    private void validateNull(GpsData other) {
        if (Objects.isNull(other)) {
            throw new InvalidGpsDataException();
        }
    }

    private void validateIsBeforeData(LocalDateTime time) {
        if (this.time.isAfter(time)) {
            throw new PastGpsDataTimeException(this.time, time);
        }
    }

    @Override
    public int compareTo(GpsData o) {
        return this.time.compareTo(o.time);
    }
}
