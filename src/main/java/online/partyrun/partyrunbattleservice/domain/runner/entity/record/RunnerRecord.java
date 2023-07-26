package online.partyrun.partyrunbattleservice.domain.runner.entity.record;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.runner.exception.IllegalRecordDistanceException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidGpsDataException;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RunnerRecord implements Comparable<RunnerRecord> {
    private static final double MIN_DISTANCE = 0;

    GpsData gpsData;
    double distance;

    public RunnerRecord(GpsData gpsData, double distance) {
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

    public RunnerRecord createNewRecord(GpsData gpsData) {
        double distance = this.gpsData.calculateDistance(gpsData);

        return new RunnerRecord(gpsData, this.distance + distance);
    }

    @Override
    public int compareTo(RunnerRecord o) {
        return this.gpsData.compareTo(o.gpsData);
    }
}
