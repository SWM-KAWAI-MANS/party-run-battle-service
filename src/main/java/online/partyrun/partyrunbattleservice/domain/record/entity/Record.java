package online.partyrun.partyrunbattleservice.domain.record.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Record {
    private static final double MIN_DISTANCE = 0;

    GpsData gpsData;
    double distance;

    public Record(GpsData gpsData, double distance) {
        validateDistance(distance);
        this.gpsData = gpsData;
        this.distance = distance;
    }

    private void validateDistance(double distance) {
        if (distance < MIN_DISTANCE) {
            // TODO: 2023/07/21 예외 변경
            throw new IllegalArgumentException();
        }
    }

    public Record createNewRecord(GpsData gpsData) {
        double distance = this.gpsData.calculateDistance(gpsData);

        return new Record(gpsData, this.distance + distance);
    }
}
