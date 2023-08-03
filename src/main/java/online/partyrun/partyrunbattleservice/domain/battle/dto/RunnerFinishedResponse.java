package online.partyrun.partyrunbattleservice.domain.battle.dto;

import java.util.Map;

public class RunnerFinishedResponse extends BattleWebSocketResponse {
    public RunnerFinishedResponse(String runnerId) {
        super("RUNNER_FINISHED", Map.of("runnerId", runnerId));
    }
}
