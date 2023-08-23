package online.partyrun.partyrunbattleservice.domain.runner.entity.record;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.runner.exception.IllegalRecordDistanceException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidGpsDataException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RunnerRecord implements Comparable<RunnerRecord> {
    private static final double MIN_DISTANCE = 0;
    private static final double MIN_DURATION_SECOND = 0;
    private static final double MAXIMUM_RUNNER_SPEED_OF_METERS_PER_SECOND = 12.42;

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

    public Optional<RunnerRecord> createNextRecord(GpsData gpsData) {
        final double movingDistance = this.gpsData.calculateDistance(gpsData);
        final double durationSeconds = this.gpsData.calculateDuration(gpsData);

        if (movingDistance == MIN_DURATION_SECOND) {
            return Optional.empty();
        }

        final double runnerSpeedOfMetersPerSecond = movingDistance / durationSeconds;
        if (MAXIMUM_RUNNER_SPEED_OF_METERS_PER_SECOND >= runnerSpeedOfMetersPerSecond) {
            return Optional.of(new RunnerRecord(gpsData, this.distance + movingDistance));
        }

        return Optional.empty();
    }

    public boolean hasBiggerDistanceThan(int targetDistance) {
        return this.distance > targetDistance;
    }

    @Override
    public int compareTo(RunnerRecord o) {
        return this.gpsData.compareTo(o.gpsData);
    }

    public LocalDateTime getTime() {
        return this.gpsData.getTime();
    }
}
