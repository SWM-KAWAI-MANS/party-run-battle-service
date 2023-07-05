package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class BattleAlreadyFinishedException extends BadRequestException {
    public BattleAlreadyFinishedException(String battleId) {
        super(String.format("%s의 battle은 이미 Finished 상태입니다.", battleId));
    }
}
