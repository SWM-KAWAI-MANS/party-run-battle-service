package online.partyrun.partyrunbattleservice.domain.battle.dto;

import java.util.Map;

public class RunnerDistanceResponse extends BattleWebSocketResponse {
    private static final String EVENT_TYPE = "BATTLE_RUNNING";
    private static final String RUNNER_ID_KEY = "runnerId";
    private static final String DISTANCE_KEY = "distance";

    public RunnerDistanceResponse(String battleId, String runnerId, double distance) {
        super(EVENT_TYPE, battleId, Map.of(RUNNER_ID_KEY, runnerId, DISTANCE_KEY, distance));
    }

    @Override
    public String toString() {
        final Map<String, Object> data = getData();
        return String.format("배틀 %s에 속한 러너 %s가 달린 거리는 %f 입니다.", getBattleId(), data.get(RUNNER_ID_KEY), data.get(DISTANCE_KEY));
    }
}

