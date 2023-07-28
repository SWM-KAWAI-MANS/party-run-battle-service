package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class RunnerAlreadyRunningException extends BadRequestException {
    public RunnerAlreadyRunningException(String runnerId) {
        super(String.format("%s의 runner는 이미 Running 상태입니다.", runnerId));
    }
}
