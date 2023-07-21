package online.partyrun.partyrunbattleservice.domain.record.dto;

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

    @Override
    public String toString() {
        return "RunnerDistanceResponse{"
                + "type='"
                + type
                + '\''
                + ", runnerId='"
                + runnerId
                + '\''
                + ", isFinished="
                + isFinished
                + ", distance="
                + distance
                + '}';
    }
}
