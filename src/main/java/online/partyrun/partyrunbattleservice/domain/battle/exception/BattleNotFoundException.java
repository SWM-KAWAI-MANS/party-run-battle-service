package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.NotFoundException;

public class BattleNotFoundException extends NotFoundException {
    public BattleNotFoundException(String battleId) {
        super(String.format("%s에 해당하는 배틀이 존재하지 않습니다.", battleId));
    }

    public BattleNotFoundException(String battleId, String runnerId) {
        super(String.format("%s 배틀에 %s 러너가 존재하는 배틀이 존재하지 않습니다.", battleId, runnerId));
    }
}
