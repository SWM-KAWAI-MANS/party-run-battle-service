package online.partyrun.partyrunbattleservice.domain.runner.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerAlreadyFinishedException;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerStatusCannotBeChangedException;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Runner {
    String id;
    RunnerStatus status = RunnerStatus.READY;

    public Runner(String id) {
        this.id = id;
    }

    public boolean hasId(String id) {
        return this.id.equals(id);
    }

    public void changeStatus(RunnerStatus status) {
        validateCurrentStatus();
        validateRunnerStatus(status);

        this.status = status;
    }

    private void validateCurrentStatus() {
        if (this.status.isFinished()) {
            throw new RunnerAlreadyFinishedException(this.id);
        }
    }

    private void validateRunnerStatus(RunnerStatus status) {
        if (Objects.isNull(status) || status.isReady() || this.status.equals(status)) {
            throw new RunnerStatusCannotBeChangedException(status);
        }
    }
}
