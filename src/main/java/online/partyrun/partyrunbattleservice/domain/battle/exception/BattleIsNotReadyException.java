package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class BattleIsNotReadyException extends BadRequestException {
    public BattleIsNotReadyException(String battleId) {
        super(String.format("%s 배틀은 READY 상태가 아닙니다.", battleId));
    }
}
