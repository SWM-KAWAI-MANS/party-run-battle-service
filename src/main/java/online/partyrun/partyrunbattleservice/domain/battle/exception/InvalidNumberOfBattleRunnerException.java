package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class InvalidNumberOfBattleRunnerException extends BadRequestException {
    public InvalidNumberOfBattleRunnerException(int runnerNumber) {
        super(String.format("battle 참여 인원은 %d일 수 없습니다.", runnerNumber));
    }
}
