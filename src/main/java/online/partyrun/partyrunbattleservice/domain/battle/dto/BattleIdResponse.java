package online.partyrun.partyrunbattleservice.domain.battle.dto;

public record BattleIdResponse(String id) {

    @Override
    public String toString() {
        return String.format("배틀의 id는 %s 입니다.", id);
    }
}
