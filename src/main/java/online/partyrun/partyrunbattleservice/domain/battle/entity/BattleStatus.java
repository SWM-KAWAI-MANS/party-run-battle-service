package online.partyrun.partyrunbattleservice.domain.battle.entity;

public enum BattleStatus {
    READY,
    RUNNING,
    FINISHED;

    public boolean isFinished() {
        return this == FINISHED;
    }
}
