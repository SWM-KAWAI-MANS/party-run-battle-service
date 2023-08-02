package online.partyrun.partyrunbattleservice.domain.battle.dto;

public record RunnerFinishedResponse(String type, String runnerId) {

    public RunnerFinishedResponse(String runnerId) {
        this("BATTLE_FINISHED", runnerId);
    }
}
