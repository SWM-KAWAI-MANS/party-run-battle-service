package online.partyrun.partyrunbattleservice.domain.single.dto;

import online.partyrun.partyrunbattleservice.domain.runner.dto.RunnerRecordResponse;
import online.partyrun.partyrunbattleservice.domain.single.entity.Single;

import java.util.List;

public record SingleResponse(RunningTimeResponse runningTime, List<RunnerRecordResponse> records) {

    public SingleResponse(Single single) {
        this(new RunningTimeResponse(single.getRunningTime()), single.getRunnerRecords().stream().map(RunnerRecordResponse::new).toList());
    }
}
