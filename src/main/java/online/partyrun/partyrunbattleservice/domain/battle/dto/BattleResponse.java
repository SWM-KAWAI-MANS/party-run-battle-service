package online.partyrun.partyrunbattleservice.domain.battle.dto;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.runner.dto.RunnerResponse;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record BattleResponse(double targetDistance, LocalDateTime startTime, List<RunnerResponse> runners) {

    public static BattleResponse from(Battle battle) {
        final List<Runner> runnersOrderByRank = battle.getRunnersOrderByRank();
        return new BattleResponse(battle.getTargetDistance(), battle.getStartTime(), toRunnerResponses(runnersOrderByRank));
    }

    private static List<RunnerResponse> toRunnerResponses(List<Runner> runners) {
        final List<RunnerResponse> result = new ArrayList<>();

        int rank = 1;
        for (Runner runner : runners) {
            RunnerResponse response = new RunnerResponse(rank++, runner);
            result.add(response);
        }

        return result;
    }
}
