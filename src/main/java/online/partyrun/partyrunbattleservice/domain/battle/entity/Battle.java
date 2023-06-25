package online.partyrun.partyrunbattleservice.domain.battle.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.exception.InvalidNumberOfBattleRunnerException;
import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;

import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Battle {
    static final int RUNNER_SIZE = 3;

    @Id String id;

    List<Runner> runners;

    public Battle(List<Runner> runners) {
        validateRunners(runners);
        this.runners = runners;
    }

    private void validateRunners(List<Runner> runners) {
        if (runners.size() != RUNNER_SIZE) {
            throw new InvalidNumberOfBattleRunnerException(runners.size());
        }
    }
}
