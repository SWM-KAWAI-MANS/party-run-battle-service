package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.NotFoundException;

public class ReadyRunnerNotFoundException extends NotFoundException {
    public ReadyRunnerNotFoundException(String runnerId) {
        super(String.format("%s는 현재 진행중인 battle에 참여하고 있지 않습니다.", runnerId));
    }
}
