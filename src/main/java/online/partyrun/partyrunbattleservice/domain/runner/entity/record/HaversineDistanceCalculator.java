package online.partyrun.partyrunbattleservice.domain.runner.entity.record;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

public class HaversineDistanceCalculator implements DistanceCalculator<GeoJsonPoint, Double> {
    private static final double EARTH_RADIUS = 6371009;

    @Override
    public Double calculate(GeoJsonPoint point1, GeoJsonPoint point2) {
        final double lat1 = point1.getY();
        final double lng1 = point1.getX();
        final double lat2 = point2.getY();
        final double lhg2 = point2.getX();

        return EARTH_RADIUS * distanceRadians(lat1, lng1, lat2, lhg2);
    }

    private double distanceRadians(double lat1, double lng1, double lat2, double lng2) {
        return arcHav(havDistance(Math.toRadians(lat1), Math.toRadians(lat2), Math.toRadians(lng1) - Math.toRadians(lng2)));
    }

    private double havDistance(double lat1, double lat2, double dLng) {
        return hav(lat1 - lat2) + hav(dLng) * Math.cos(lat1) * Math.cos(lat2);
    }

    private double hav(double x) {
        double sinHalf = Math.sin(x * 0.5);
        return sinHalf * sinHalf;
    }

    private double arcHav(double x) {
        return 2 * Math.asin(Math.sqrt(x));
    }
}