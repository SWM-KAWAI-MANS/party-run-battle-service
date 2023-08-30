package online.partyrun.partyrunbattleservice.domain.runner.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.runner.exception.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Runner {
    private static final int DEFAULT_DISTANCE = 0;
    private static final int FIRST_GPS_DATA_INDEX = 0;

    String id;
    RunnerStatus status = RunnerStatus.READY;
    RunnerRecord recentRunnerRecord;
    List<RunnerRecord> runnerRecords = new ArrayList<>();

    public Runner(String id) {
        this.id = id;
    }

    public boolean hasId(String id) {
        return this.id.equals(id);
    }

    public void changeRunningStatus() {
        validateIsNotReadyStatus();

        this.status = RunnerStatus.RUNNING;
    }

    private void validateIsNotReadyStatus() {
        if (!status.isReady()) {
            throw new RunnerIsNotReadyException(this.id);
        }
    }

    public boolean isRunning() {
        return this.status.isRunning();
    }

    public boolean isFinished() {
        return this.status.isFinished();
    }

    public void addRecords(List<GpsData> gpsData) {
        validateIsNotRunningStatus();

        final List<RunnerRecord> newRecords = createRecords(gpsData);

        this.runnerRecords.addAll(newRecords);
        this.recentRunnerRecord = Collections.max(this.runnerRecords);
    }

    private void validateIsNotRunningStatus() {
        if (!status.isRunning()) {
            throw new RunnerIsNotRunningException(this.id);
        }
    }

    private List<RunnerRecord> createRecords(List<GpsData> gpsData) {
        final List<GpsData> copiedGpsData = new ArrayList<>(gpsData);
        Collections.sort(copiedGpsData);

        if (hasNotRecentRecord()) {
            final GpsData firstGpsData = copiedGpsData.get(FIRST_GPS_DATA_INDEX);
            final RunnerRecord firstRecord = new RunnerRecord(firstGpsData, DEFAULT_DISTANCE);
            this.runnerRecords.add(firstRecord);

            return createRecords(firstRecord, copiedGpsData);
        }

        return createRecords(this.recentRunnerRecord, copiedGpsData);
    }

    private List<RunnerRecord> createRecords(RunnerRecord record, List<GpsData> gpsData) {
        List<RunnerRecord> records = new ArrayList<>();

        for (GpsData newGpsData : gpsData) {
            final Optional<RunnerRecord> newRecord = record.createNextRecord(newGpsData);
            if (newRecord.isPresent()) {
                record = newRecord.get();
                records.add(record);
            }
        }

        return records;
    }

    public double getRecentDistance() {
        validateRecentRunnerRecords();
        return this.recentRunnerRecord.getDistance();
    }

    private void validateRecentRunnerRecords() {
        if (hasNotRecentRecord()) {
            throw new InvalidRecentRunnerRecordException(this.id);
        }
    }

    private boolean hasNotRecentRecord() {
        return Objects.isNull(this.recentRunnerRecord);
    }

    public boolean isRunningMoreThan(int targetDistance) {
        if (hasNotRecentRecord()) {
            return false;
        }
        return this.recentRunnerRecord.hasBiggerDistanceThan(targetDistance);
    }

    public void changeFinishStatus() {
        validateIsNotRunningStatus();

        this.status = RunnerStatus.FINISHED;
    }

    public LocalDateTime getLastRecordTime() {
        if (hasNotRecentRecord()) {
            return null;
        }
        return this.recentRunnerRecord.getTime();
    }

    public double getDistance() {
        if (hasNotRecentRecord()) {
            return 0;
        }
        return this.recentRunnerRecord.getDistance();
    }

    public int compareToLastRecordTime(Runner other) {
        return this.recentRunnerRecord.compareTo(other.recentRunnerRecord);
    }
}
