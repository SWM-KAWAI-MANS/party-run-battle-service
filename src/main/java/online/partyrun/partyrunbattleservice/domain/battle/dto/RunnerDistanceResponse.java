package online.partyrun.partyrunbattleservice.domain.battle.dto;

public record RunnerDistanceResponse(String type, String runnerId, double distance) {

    public RunnerDistanceResponse(String runnerId, double distance) {
        this("BATTLE_RUNNING", runnerId, distance);
    }
}
