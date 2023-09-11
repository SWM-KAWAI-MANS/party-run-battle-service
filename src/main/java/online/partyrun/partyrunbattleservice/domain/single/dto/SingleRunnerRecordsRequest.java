package online.partyrun.partyrunbattleservice.domain.single.dto;

import jakarta.validation.constraints.NotNull;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;

import java.util.List;

public record SingleRunnerRecordsRequest(@NotNull List<SingleRunnerRecordRequest> records) {
    public List<RunnerRecord> toRunnerRecords() {
        return this.records.stream()
                .map(SingleRunnerRecordRequest::toRunnerRecord)
                .toList();
    }
}
