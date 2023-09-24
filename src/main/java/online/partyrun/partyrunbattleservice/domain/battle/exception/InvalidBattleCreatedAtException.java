package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class InvalidBattleCreatedAtException extends BadRequestException {

    public InvalidBattleCreatedAtException() {
        super("배틀 생성 시간은 null일 수 없습니다.");
    }
}
