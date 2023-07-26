package online.partyrun.partyrunbattleservice.domain.battle.dto;

public record RunnerDistanceResponse(
        String type, String runnerId, boolean isFinished, double distance) {

    public RunnerDistanceResponse(String runnerId, boolean isFinished, double distance) {
        this("BATTLE_RUNNING", runnerId, isFinished, distance);
    }
}
