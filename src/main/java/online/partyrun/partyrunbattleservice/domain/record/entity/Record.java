package online.partyrun.partyrunbattleservice.domain.record.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.record.exception.IllegalRecordDistanceException;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Record {
    private static final double MIN_DISTANCE = 0;

    GpsData gpsData;
    double distance;

    public Record(GpsData gpsData, double distance) {
        validateGpsData(gpsData);
        validateDistance(distance);
        this.gpsData = gpsData;
        this.distance = distance;
    }

    private void validateGpsData(GpsData gpsData) {
        if (Objects.isNull(gpsData)) {
            throw new InvalidGpsDataException();
        }
    }

    private void validateDistance(double distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalRecordDistanceException(distance);
        }
    }

    public Record createNewRecord(GpsData gpsData) {
        double distance = this.gpsData.calculateDistance(gpsData);

        return new Record(gpsData, this.distance + distance);
    }
}
