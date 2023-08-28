package online.partyrun.partyrunbattleservice.domain.battle.dto;

import java.util.Map;

public class RunnerDistanceResponse extends BattleWebSocketResponse {

    public RunnerDistanceResponse(String battleId, String runnerId, double distance) {
        super("BATTLE_RUNNING", battleId, Map.of("runnerId", runnerId, "distance", distance));
    }
}
