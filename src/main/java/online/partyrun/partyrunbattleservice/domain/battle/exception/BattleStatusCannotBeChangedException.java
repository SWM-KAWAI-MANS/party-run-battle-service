package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.domain.battle.entity.BattleStatus;
import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class BattleStatusCannotBeChangedException extends BadRequestException {
    public BattleStatusCannotBeChangedException(BattleStatus status) {
        super(String.format("배틀의 상태는 %s로 변경할 수 없습니다.", status));
    }
}
