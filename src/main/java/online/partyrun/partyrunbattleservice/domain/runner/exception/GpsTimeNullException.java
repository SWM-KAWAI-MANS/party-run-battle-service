package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class GpsTimeNullException extends BadRequestException {

    public GpsTimeNullException() {
        super("GpsTime은 null일 수 없습니다.");
    }
}
