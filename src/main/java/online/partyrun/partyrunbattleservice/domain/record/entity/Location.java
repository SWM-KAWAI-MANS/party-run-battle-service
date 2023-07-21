package online.partyrun.partyrunbattleservice.domain.record.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    private static final double EARTH_RADIUS = 6371;

    GeoJsonPoint point;
    double altitude;

    public static Location of(double longitude, double latitude, double altitude) {
        return new Location(new GeoJsonPoint(longitude, latitude), altitude);
    }

    public double calculateDistance(Location other) {
        final double lon1 = this.point.getX();
        final double lat1 = this.point.getY();
        final double lon2 = other.point.getX();
        final double lat2 = other.point.getY();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c * 1000;
    }
}
