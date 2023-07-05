package online.partyrun.partyrunbattleservice.domain.runner.entity;

public enum RunnerStatus {
    READY,
    RUNNING,
    FINISHED;

    public boolean isReady() {
        return this == READY;
    }
}
