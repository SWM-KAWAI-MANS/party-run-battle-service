package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class RunnerAlreadyFinishedException extends BadRequestException {
    public RunnerAlreadyFinishedException(String runnerId) {
        super(String.format("%s의 runner는 이미 Finished 상태입니다.", runnerId));
    }
}
