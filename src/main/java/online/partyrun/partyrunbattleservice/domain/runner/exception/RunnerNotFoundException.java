package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.global.exception.NotFoundException;

public class RunnerNotFoundException extends NotFoundException {
    public RunnerNotFoundException(String runnerId) {
        super(String.format("%s는 존재하지 않는 러너입니다.", runnerId));
    }
}
