package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class BattleNotStartedException extends BadRequestException {
    public BattleNotStartedException() {
        super(String.format("배틀이 아직 시작하지 않았습니다."));
    }
}
