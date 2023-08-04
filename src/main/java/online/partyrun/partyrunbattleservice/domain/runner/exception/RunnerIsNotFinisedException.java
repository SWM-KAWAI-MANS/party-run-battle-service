package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class RunnerIsNotFinisedException extends BadRequestException {
    public RunnerIsNotFinisedException(String runnerId) {
        super(String.format("%s의 러너는 현재 종료 상태가 아닙니다.", runnerId));
    }
}
