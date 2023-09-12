package online.partyrun.partyrunbattleservice.domain.single.dto;

import jakarta.validation.constraints.NotNull;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.single.entity.RunningTime;

import java.util.List;

public record SingleRunnerRecordsRequest(@NotNull RunningTimeRequest runningTime, @NotNull List<SingleRunnerRecordRequest> records) {

    public RunningTime getRunningTime() {
        return runningTime.toRunningTime();
    }

    public List<RunnerRecord> getRunnerRecords() {
        return this.records.stream()
                .map(SingleRunnerRecordRequest::toRunnerRecord)
                .toList();
    }
}
