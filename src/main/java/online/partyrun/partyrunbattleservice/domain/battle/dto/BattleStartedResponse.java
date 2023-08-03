package online.partyrun.partyrunbattleservice.domain.battle.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class BattleStartedResponse extends BattleWebSocketResponse {

    public BattleStartedResponse(LocalDateTime startTime) {
        super("BATTLE_START", Map.of("startTime", startTime));
    }
}
