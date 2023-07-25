package online.partyrun.partyrunbattleservice.domain.battle.dto;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RunnerDistanceResponse {
    String type;
    String runnerId;
    boolean isFinished;
    double distance;

    public RunnerDistanceResponse(String runnerId, boolean isFinished, double distance) {
        this.type = "BATTLE_RUNNING";
        this.runnerId = runnerId;
        this.isFinished = isFinished;
        this.distance = distance;
    }
}
