package online.partyrun.partyrunbattleservice.domain.runner.entity.record;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.exception.IllegalLocationException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.DistanceCalculatorEmptyException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.LocationEmptyException;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    private static final double MAX_LONGITUDE = 180;
    private static final double MIN_LONGITUDE = -180;
    private static final double MAX_LATITUDE = 90;
    private static final double MIN_LATITUDE = -90;
    private static final double MAX_ALTITUDE = 50000;
    private static final double MIN_ALTITUDE = 0;

    GeoJsonPoint point;
    double altitude;

    public static Location of(double longitude, double latitude, double altitude) {
        validateValue(longitude, MAX_LONGITUDE, MIN_LONGITUDE);
        validateValue(latitude, MAX_LATITUDE, MIN_LATITUDE);
        validateValue(altitude, MAX_ALTITUDE, MIN_ALTITUDE);

        return new Location(new GeoJsonPoint(longitude, latitude), altitude);
    }

    private static void validateValue(double value, double maxValue, double minValue) {
        if (value < minValue || value > maxValue) {
            throw new IllegalLocationException(value, maxValue, minValue);
        }
    }

    public double calculateDistance(
            Location other, DistanceCalculator<GeoJsonPoint, Double> distanceCalculator) {
        validateLocation(other);
        validateCalculator(distanceCalculator);

        return distanceCalculator.calculate(this.point, other.point);
    }

    private void validateLocation(Location other) {
        if (Objects.isNull(other)) {
            throw new LocationEmptyException();
        }
    }

    private void validateCalculator(DistanceCalculator<GeoJsonPoint, Double> distanceCalculator) {
        if (Objects.isNull(distanceCalculator)) {
            throw new DistanceCalculatorEmptyException();
        }
    }
}
