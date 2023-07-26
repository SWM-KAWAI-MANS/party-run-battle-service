package online.partyrun.partyrunbattleservice.domain.runner.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidRecentRunnerRecordException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerAlreadyFinishedException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerIsNotRunningException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerStatusCannotBeChangedException;

import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Runner {
    String id;
    RunnerStatus status;
    RunnerRecord recentRunnerRecord;
    List<RunnerRecord> runnerRecords = new ArrayList<>();

    public Runner(String id) {
        this.id = id;
        this.status = RunnerStatus.READY;
    }

    public boolean hasId(String id) {
        return this.id.equals(id);
    }

    public void changeStatus(RunnerStatus status) {
        validateIsFinishedStatus();
        validateRunnerStatus(status);

        this.status = status;
    }

    private void validateIsFinishedStatus() {
        if (this.status.isFinished()) {
            throw new RunnerAlreadyFinishedException(this.id);
        }
    }

    private void validateRunnerStatus(RunnerStatus status) {
        if (Objects.isNull(status) || status.isReady() || this.status.equals(status)) {
            throw new RunnerStatusCannotBeChangedException(status);
        }
    }

    public boolean isRunning() {
        return this.status.isRunning();
    }

    public void createNewRecords(List<GpsData> gpsData) {
        validateIsRunningStatus();

        final List<RunnerRecord> newRecords = createRecords(gpsData);

        this.runnerRecords.addAll(newRecords);
        this.recentRunnerRecord = Collections.max(newRecords);
    }

    private void validateIsRunningStatus() {
        if (!status.isRunning()) {
            throw new RunnerIsNotRunningException(this.id);
        }
    }

    private List<RunnerRecord> createRecords(List<GpsData> gpsData) {
        final List<GpsData> copiedGpsData = new ArrayList<>(gpsData);
        Collections.sort(copiedGpsData);

        if (Objects.isNull(this.recentRunnerRecord)) {
            final GpsData firstGpsData = copiedGpsData.get(0);
            final RunnerRecord firstRecord = new RunnerRecord(firstGpsData, 0);

            return createRecords(firstRecord, copiedGpsData);
        }

        return createRecords(this.recentRunnerRecord, copiedGpsData);
    }

    private List<RunnerRecord> createRecords(RunnerRecord record, List<GpsData> gpsData) {
        List<RunnerRecord> records = new ArrayList<>();
        for (GpsData newGpsData : gpsData) {
            record = record.createNewRecord(newGpsData);
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
