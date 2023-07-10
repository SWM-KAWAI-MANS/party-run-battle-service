package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class InvalidRunnerNumberInBattleException extends BadRequestException {
    public InvalidRunnerNumberInBattleException() {
        super(String.join("러너는 1명 이상이어야 합니다."));
    }
}
