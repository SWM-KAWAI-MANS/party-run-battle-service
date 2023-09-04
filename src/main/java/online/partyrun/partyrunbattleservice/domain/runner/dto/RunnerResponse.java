package online.partyrun.partyrunbattleservice.domain.runner.dto;

import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;

import java.time.LocalDateTime;
import java.util.List;

public record RunnerResponse(String id, int rank, LocalDateTime endTime, List<RunnerRecordResponse> records) {

    public RunnerResponse(int rank, Runner runner) {
        this(runner.getId(), rank, runner.getLastRecordTime(), runner.getRunnerRecords().stream().map(RunnerRecordResponse::new).toList());
    }

    @Override
    public String toString() {
        return String.format("RunnerResponse{id: %s, rank: %d, endTime: %s}", id, rank, endTime);
    }
}
