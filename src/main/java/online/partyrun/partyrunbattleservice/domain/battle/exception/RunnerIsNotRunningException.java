package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class RunnerIsNotRunningException extends BadRequestException {
    public RunnerIsNotRunningException(String battleId, String runnerId) {
        super(String.format("%s 배틀의 러너 %s 는 RUNNING 상태가 아닙니다.", battleId, runnerId));
    }
}
