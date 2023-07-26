package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class RunnerIsNotRunningException extends BadRequestException {
    public RunnerIsNotRunningException(String id) {
        super(String.format("%s 러너는 RUNNING 상태가 아닙니다.", id));
    }
}
