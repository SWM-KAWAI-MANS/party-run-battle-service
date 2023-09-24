package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class RunnerStatusCannotBeChangedException extends BadRequestException {
    public RunnerStatusCannotBeChangedException(RunnerStatus runnerStatus) {
        super(String.format("러너의 상태는 %s로 변경할 수 없습니다.", runnerStatus));
    }
}
