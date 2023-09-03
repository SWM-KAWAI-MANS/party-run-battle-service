package online.partyrun.partyrunbattleservice.domain.battle.dto;

import java.util.Map;

public class RunnerFinishedResponse extends BattleWebSocketResponse {
    private static final String EVENT_TYPE = "RUNNER_FINISHED";
    private static final String RUNNER_ID_KEY = "runnerId";
    public RunnerFinishedResponse(String battleId, String runnerId) {
        super(EVENT_TYPE, battleId, Map.of(RUNNER_ID_KEY, runnerId));
    }

    @Override
    public String toString() {
        return String.format("배틀 %s에 속한 러너 %s가 종료하였습니다.", getBattleId(), getData().get(RUNNER_ID_KEY));
    }
}
