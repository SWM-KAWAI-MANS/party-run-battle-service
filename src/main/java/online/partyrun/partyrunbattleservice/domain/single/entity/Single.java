package online.partyrun.partyrunbattleservice.domain.single.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.single.exception.SingleRunnerRecordEmptyException;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Single {

    @Id
    String id;
    String runnerId;
    RunningTime runningTime;
    List<RunnerRecord> runnerRecords;

    public Single(String runnerId, RunningTime runningTime, List<RunnerRecord> runnerRecords) {
        validateRunnerRecords(runnerRecords);
        this.runnerId = runnerId;
        this.runningTime = runningTime;
        this.runnerRecords = runnerRecords;
    }

    private void validateRunnerRecords(List<RunnerRecord> runnerRecords) {
        if (Objects.isNull(runnerRecords) || runnerRecords.isEmpty()) {
            throw new SingleRunnerRecordEmptyException();
        }
    }
}
