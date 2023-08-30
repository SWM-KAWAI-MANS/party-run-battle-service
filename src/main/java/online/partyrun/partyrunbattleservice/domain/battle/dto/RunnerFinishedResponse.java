package online.partyrun.partyrunbattleservice.domain.battle.dto;

import java.util.Map;

public class RunnerFinishedResponse extends BattleWebSocketResponse {
    public RunnerFinishedResponse(String battleId, String runnerId) {
        super("RUNNER_FINISHED", battleId, Map.of("runnerId", runnerId));
    }
}
