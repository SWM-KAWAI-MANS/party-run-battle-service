package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class RunnerIsNotReadyException extends BadRequestException {
    public RunnerIsNotReadyException(String runnerId) {
        super(String.format("%s의 runner는 Ready 상태가 아닙니다.", runnerId));
    }
}
