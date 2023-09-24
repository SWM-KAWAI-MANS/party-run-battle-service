package online.partyrun.partyrunbattleservice.domain.single.dto;

import online.partyrun.partyrunbattleservice.domain.single.entity.RunningTime;

public record RunningTimeResponse(int hours, int minutes, int seconds) {

    public RunningTimeResponse(RunningTime runningTime) {
        this(runningTime.getHours(), runningTime.getMinutes(), runningTime.getSeconds());
    }
}
