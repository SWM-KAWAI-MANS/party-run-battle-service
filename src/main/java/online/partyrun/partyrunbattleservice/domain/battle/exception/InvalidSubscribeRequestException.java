package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class InvalidSubscribeRequestException extends BadRequestException {
    public InvalidSubscribeRequestException(String battleId, String runnerId) {
        super(String.format("%s 러너는 %s 배틀을 구독할 수 없습니다.", runnerId, battleId));
    }
}
