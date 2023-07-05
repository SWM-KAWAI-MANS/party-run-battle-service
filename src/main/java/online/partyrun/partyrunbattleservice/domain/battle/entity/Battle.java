package online.partyrun.partyrunbattleservice.domain.battle.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.exception.InvalidDistanceException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.InvalidRunnerNumberInBattleException;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;

import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerNotFoundException;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Battle {
    static int MIN_DISTANCE = 1;

    @Id String id;
    int distance;
    List<Runner> runners;
    BattleStatus status = BattleStatus.READY;
    @CreatedDate LocalDateTime createdAt;

    public Battle(int distance, List<Runner> runners) {
        validateDistance(distance);
        validateRunners(runners);
        this.distance = distance;
        this.runners = runners;
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

    public void changeRunnerStatus(String runnerId, RunnerStatus runnerStatus) {
        final Runner runner = findRunner(runnerId);
        runner.changeStatus(runnerStatus);
    }

    private Runner findRunner(String runnerId) {
        return runners.stream()
                .filter(r -> r.getId().equals(runnerId))
                .findFirst()
                .orElseThrow(() -> new RunnerNotFoundException(runnerId));
    }

    public void changeStatus(BattleStatus status) {
        this.status = Objects.requireNonNull(status);
    }
}
