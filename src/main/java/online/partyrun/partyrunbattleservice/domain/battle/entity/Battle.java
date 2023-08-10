package online.partyrun.partyrunbattleservice.domain.battle.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.exception.*;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidGpsDataException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.InvalidGpsDataTimeException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerNotFoundException;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Battle {
    private static final int MIN_DISTANCE = 1;
    private static final int ADDED_SECOND_FOR_START_TIME = 5;

    @Id String id;
    int targetDistance;
    List<Runner> runners;
    LocalDateTime startTime;
    LocalDateTime createdAt;

    public Battle(int targetDistance, List<Runner> runners, LocalDateTime createdAt) {
        validateDistance(targetDistance);
        validateRunners(runners);
        validateCreatedAt(createdAt);
        this.targetDistance = targetDistance;
        this.runners = runners;
        this.createdAt = createdAt;
    }

    private void validateCreatedAt(LocalDateTime createdAt) {
        if (Objects.isNull(createdAt)) {
            throw new InvalidBattleCreatedAtException();
        }
    }

    private void validateDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new InvalidDistanceException(distance, MIN_DISTANCE);
        }
    }

    private void validateRunners(List<Runner> runners) {
        if (Objects.isNull(runners) || runners.isEmpty()) {
            throw new InvalidRunnerNumberInBattleException();
        }
    }

    public void changeRunnerRunningStatus(String runnerId) {
        final Runner runner = findRunner(runnerId);
        runner.changeRunningStatus();
    }

    private Runner findRunner(String runnerId) {
        return runners.stream()
                .filter(runner -> runner.hasId(runnerId))
                .findFirst()
                .orElseThrow(() -> new RunnerNotFoundException(runnerId));
    }

    public void setStartTime(LocalDateTime now) {
        validateAllRunnersRunning();

        this.startTime = now.plusSeconds(ADDED_SECOND_FOR_START_TIME);
    }

    private void validateAllRunnersRunning() {
        if (!isAllRunnersRunningStatus()) {
            throw new AllRunnersAreNotRunningStatusException(
                    runners.stream().map(Runner::getId).toList());
        }
    }

    public boolean isAllRunnersRunningStatus() {
        return this.runners.stream().allMatch(Runner::isRunning);
    }

    public RunnerStatus getRunnerStatus(String runnerId) {
        final Runner runner = findRunner(runnerId);
        return runner.getStatus();
    }

    public void addRecords(String runnerId, List<GpsData> gpsData) {
        validateStartTime();
        validateGpsData(gpsData);

        final Runner runner = findRunner(runnerId);
        runner.addRecords(gpsData);

        changeFinishStatusIfExceededTargetDistance(runner);
    }

    private void validateStartTime() {
        if (Objects.isNull(this.startTime)) {
            throw new BattleNotStartedException();
        }
    }

    private void validateGpsData(List<GpsData> gpsData) {
        if (Objects.isNull(gpsData) || gpsData.isEmpty()) {
            throw new InvalidGpsDataException();
        }

        if (hasBeforeStartTime(gpsData)) {
            throw new InvalidGpsDataTimeException(this.startTime);
        }
    }

    private boolean hasBeforeStartTime(List<GpsData> gpsData) {
        return gpsData.stream().anyMatch(data -> data.isBefore(this.startTime));
    }

    private void changeFinishStatusIfExceededTargetDistance(Runner runner) {
        if (runner.isRunningMoreThan(this.targetDistance)) {
            runner.changeFinishStatus();
        }
    }

    public List<RunnerRecord> getRunnerRecords(String runnerId) {
        final Runner runner = findRunner(runnerId);

        return runner.getRunnerRecords();
    }

    public double getRunnerRecentDistance(String runnerId) {
        final Runner runner = findRunner(runnerId);

        return runner.getRecentDistance();
    }

    public boolean isRunnerFinished(String runnerId) {
        final Runner runner = findRunner(runnerId);

        return runner.isFinished();
    }

    public RunnerRecord getRunnerRecentRecord(String runnerId) {
        final Runner runner = findRunner(runnerId);
        return runner.getRecentRunnerRecord();
    }

    public List<Runner> getRunnersOrderByRank() {
        Comparator<Runner> rankComparator =
                (runner1, runner2) -> {
                    boolean isFinished1 = runner1.isRunningMoreThan(targetDistance);
                    boolean isFinished2 = runner2.isRunningMoreThan(targetDistance);

                    if (isFinished1 && isFinished2) {
                        return runner1.compareToLastRecordTime(runner2);
                    } else if (isFinished1) {
                        return -1;
                    } else if (isFinished2) {
                        return 1;
                    }
                    return Double.compare(runner2.getDistance(), runner1.getDistance());
                };
        return this.runners.stream().sorted(rankComparator).toList();
    }
}
