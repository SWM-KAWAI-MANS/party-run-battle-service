package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class LocationEmptyException extends BadRequestException {

    public LocationEmptyException() {
        super("Location은 null일 수 없습니다.");
    }
}
