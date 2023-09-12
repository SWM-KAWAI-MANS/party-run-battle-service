package online.partyrun.partyrunbattleservice.domain.single.dto;

import online.partyrun.partyrunbattleservice.domain.single.entity.RunningTime;

public record RunningTimeRequest(int hours, int minutes, int seconds) {

    public RunningTime toRunningTime() {
        return new RunningTime(hours, minutes, seconds);
    }
}
