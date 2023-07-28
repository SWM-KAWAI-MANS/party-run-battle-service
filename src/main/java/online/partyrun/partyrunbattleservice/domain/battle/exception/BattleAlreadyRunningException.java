package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class BattleAlreadyRunningException extends BadRequestException {
    public BattleAlreadyRunningException(String battleId) {
        super(String.format("%s의 battle은 이미 Running 상태입니다.", battleId));
    }
}
