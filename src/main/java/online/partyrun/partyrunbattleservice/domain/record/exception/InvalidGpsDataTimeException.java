package online.partyrun.partyrunbattleservice.domain.record.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

import java.time.LocalDateTime;

public class InvalidGpsDataTimeException extends BadRequestException {
    public InvalidGpsDataTimeException(LocalDateTime time, LocalDateTime minTime) {
        super(String.format("Gps 시간 %s 는 %s 보다 이후여야합니다.", time, minTime));
    }
}
