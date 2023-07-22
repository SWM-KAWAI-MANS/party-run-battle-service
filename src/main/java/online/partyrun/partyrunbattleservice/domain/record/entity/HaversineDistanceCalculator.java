package online.partyrun.partyrunbattleservice.domain.record.entity;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

public class HaversineDistanceCalculator implements DistanceCalculator<GeoJsonPoint, Double> {
    private static final double EARTH_RADIUS = 6371;

    @Override
    public Double calculate(GeoJsonPoint point1, GeoJsonPoint point2) {
        final double lon1 = point1.getX();
        final double lat1 = point1.getY();
        final double lon2 = point2.getX();
        final double lat2 = point2.getY();

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
