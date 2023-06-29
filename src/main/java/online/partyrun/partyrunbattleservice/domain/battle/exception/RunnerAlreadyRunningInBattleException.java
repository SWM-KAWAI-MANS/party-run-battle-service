package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;
import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

import java.util.List;

public class RunnerAlreadyRunningInBattleException extends BadRequestException {
    public RunnerAlreadyRunningInBattleException(List<Runner> runners, List<Battle> runningBattle) {
        super(String.format("이미 달리고 있는 %s 에 %s가 존재합니다.", runningBattle, runners));
    }
}
