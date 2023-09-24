package online.partyrun.partyrunbattleservice.domain.battle.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BattleWebSocketResponse {

    String type;
    String battleId;
    Map<String, Object> data;
}
