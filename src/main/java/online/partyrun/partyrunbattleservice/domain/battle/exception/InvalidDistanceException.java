package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class InvalidDistanceException extends BadRequestException {
    public InvalidDistanceException(int distance, int minDistance) {
        super(String.format("뛰어야 하는 거리는 %d일 수 없습니다. (최소 %d) ", distance, minDistance));
    }
}
