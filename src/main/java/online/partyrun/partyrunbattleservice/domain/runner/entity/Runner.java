package online.partyrun.partyrunbattleservice.domain.runner.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerStatusCannotBeChangedException;
import org.springframework.data.annotation.Id;

import java.util.Objects;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Runner {
    @Id String id;
    RunnerStatus status = RunnerStatus.READY;

    public Runner(String id) {
        this.id = id;
    }

    public void changeStatus(RunnerStatus runnerStatus) {
        validateRunnerStatus(runnerStatus);
        this.status = runnerStatus;
    }

    private void validateRunnerStatus(RunnerStatus runnerStatus) {
        if (Objects.isNull(runnerStatus) || runnerStatus.isReady() || this.status.equals(runnerStatus)) {
            throw new RunnerStatusCannotBeChangedException(runnerStatus);
        }
    }
}
