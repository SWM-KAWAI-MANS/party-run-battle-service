package online.partyrun.partyrunbattleservice.domain.battle.event;

public record RunnerRunningEvent(String battleId, int targetCount) {
    public boolean isTargetCount(int eventCount) {
        return this.targetCount == eventCount;
    }
}
