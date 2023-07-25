package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

import java.time.LocalDateTime;

public class InvalidGpsDataTimeException extends BadRequestException {
    public InvalidGpsDataTimeException(LocalDateTime minTime) {
        super(String.format("Gps 시간은 최소시간인 %s 보다 이후여야합니다.", minTime));
    }
}
