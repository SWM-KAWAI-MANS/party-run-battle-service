package online.partyrun.partyrunbattleservice.domain.record.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

import java.time.LocalDateTime;

public class PastGpsDataException extends BadRequestException {
    public PastGpsDataException(LocalDateTime time, LocalDateTime pastTime) {
        super(String.format("Gps %s 시간의 데이터는 과거 Gps 시간 %s 의 데이터보다 미래여야합니다.", time, pastTime));
    }
}
