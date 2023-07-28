package online.partyrun.partyrunbattleservice.domain.runner.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerAlreadyRunningException;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidRecentRunnerRecordException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerAlreadyFinishedException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerIsNotRunningException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Runner {
    private static final int DEFAULT_DISTANCE = 0;
    private static final int FIRST_GPSDATA = 0;

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
        validateIsRunningStatus();
        validateIsFinishedStatus();

        this.status = RunnerStatus.RUNNING;
    }

    private void validateIsRunningStatus() {
        if (status.isRunning()) {
            throw new RunnerAlreadyRunningException(this.id);
        }
    }

    private void validateIsFinishedStatus() {
        if (this.status.isFinished()) {
            throw new RunnerAlreadyFinishedException(this.id);
        }
    }

    public boolean isRunning() {
        return this.status.isRunning();
    }

    public void addRecords(List<GpsData> gpsData) {
        validateIsNotRunningStatus();

        final List<RunnerRecord> newRecords = createRecords(gpsData);

        this.runnerRecords.addAll(newRecords);
        this.recentRunnerRecord = Collections.max(newRecords);
    }

    private void validateIsNotRunningStatus() {
        if (!status.isRunning()) {
            throw new RunnerIsNotRunningException(this.id);
        }
    }

    private List<RunnerRecord> createRecords(List<GpsData> gpsData) {
        final List<GpsData> copiedGpsData = new ArrayList<>(gpsData);
        Collections.sort(copiedGpsData);

        if (Objects.isNull(this.recentRunnerRecord)) {
            final GpsData firstGpsData = copiedGpsData.get(FIRST_GPSDATA);
            final RunnerRecord firstRecord = new RunnerRecord(firstGpsData, DEFAULT_DISTANCE);

            return createRecords(firstRecord, copiedGpsData);
        }

        return createRecords(this.recentRunnerRecord, copiedGpsData);
    }

    private List<RunnerRecord> createRecords(RunnerRecord record, List<GpsData> gpsData) {
        List<RunnerRecord> records = new ArrayList<>();
        for (GpsData newGpsData : gpsData) {
            record = record.createNextRecord(newGpsData);
            records.add(record);
        }

        return records;
    }

    public double getRecentDistance() {
        validateRecentRunnerRecords();
        return this.recentRunnerRecord.getDistance();
    }

    private void validateRecentRunnerRecords() {
        if (Objects.isNull(this.recentRunnerRecord)) {
            throw new InvalidRecentRunnerRecordException(this.id);
        }
    }
}
