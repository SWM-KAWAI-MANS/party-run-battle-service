package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class DistanceCalculatorEmptyException extends BadRequestException {

    public DistanceCalculatorEmptyException() {
        super("DistanceCalculator은 null일 수 없습니다.");
    }
}
