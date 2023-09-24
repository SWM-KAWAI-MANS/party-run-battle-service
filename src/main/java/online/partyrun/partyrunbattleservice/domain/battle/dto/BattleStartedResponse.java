package online.partyrun.partyrunbattleservice.domain.battle.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class BattleStartedResponse extends BattleWebSocketResponse {

    private static final String EVENT_TYPE = "BATTLE_START";
    private static final String START_TIME_KEY = "startTime";
    public BattleStartedResponse(String battleId, LocalDateTime startTime) {
        super(EVENT_TYPE, battleId, Map.of(START_TIME_KEY, startTime));
    }

    @Override
    public String toString() {
        return String.format("배틀 %s가 %s에 시작됩니다.", getBattleId(), getData().get(START_TIME_KEY));
    }
}
