package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

import java.util.List;

public class AllRunnersAreNotRunningStatusException extends BadRequestException {
    public AllRunnersAreNotRunningStatusException(List<String> runnersId) {
        super(String.format("%s 러너 중 Running 상태가 아닌 러너가 존재합니다.", runnersId));
    }
}
