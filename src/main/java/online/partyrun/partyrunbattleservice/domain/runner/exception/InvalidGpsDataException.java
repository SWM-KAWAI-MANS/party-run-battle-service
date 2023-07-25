package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class InvalidGpsDataException extends BadRequestException {

    public InvalidGpsDataException() {
        super("GpsData는 null일 수 없습니다.");
    }
}
