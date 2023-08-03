package online.partyrun.partyrunbattleservice.domain.battle.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record BattleWebSocketResponse(String type, Map<String, Object> data) {
    public static BattleWebSocketResponse createBattleStarted(LocalDateTime startTime) {
        return new BattleWebSocketResponse("BATTLE_START", Map.of("startTime", startTime));
    }

    public static BattleWebSocketResponse createRunnerDistance(String runnerId, double distance) {
        return new BattleWebSocketResponse(
                "BATTLE_RUNNING", Map.of("runnerId", runnerId, "distance", distance));
    }

    public static BattleWebSocketResponse createRunnerFinished(String runnerId) {
        return new BattleWebSocketResponse("RUNNER_FINISHED", Map.of("runnerId", runnerId));
    }
}
