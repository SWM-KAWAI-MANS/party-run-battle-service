package online.partyrun.partyrunbattleservice.domain.battle.entity;

public enum BattleStatus {
    READY,
    RUNNING,
    FINISHED;

    public boolean isReady() {
        return this == READY;
    }

    public boolean isFinished() {
        return this == FINISHED;
    }
}
