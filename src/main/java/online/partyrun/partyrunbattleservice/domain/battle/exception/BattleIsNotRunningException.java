package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class BattleIsNotRunningException extends BadRequestException {
    public BattleIsNotRunningException(String battleId) {
        super(String.format("%s 배틀은 Running 상태가 아닙니다.", battleId));
    }
}
