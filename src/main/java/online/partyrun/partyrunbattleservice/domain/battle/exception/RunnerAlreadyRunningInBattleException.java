package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

import java.util.List;

public class RunnerAlreadyRunningInBattleException extends BadRequestException {
    public RunnerAlreadyRunningInBattleException(List<String> runners) {
        super(String.format("이미 달리고 있는 배틀에 %s가 존재합니다.", runners));
    }
}
