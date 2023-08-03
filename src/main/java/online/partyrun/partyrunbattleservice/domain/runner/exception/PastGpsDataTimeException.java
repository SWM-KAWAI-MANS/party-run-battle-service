package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

import java.time.LocalDateTime;

public class PastGpsDataTimeException extends BadRequestException {
    public PastGpsDataTimeException(LocalDateTime recentDataTime, LocalDateTime gpsDataTime) {
        super(String.format("추가될 GpsData 시간 %s 는 최근 GpsData 시간 %s 보다 같거나 이후여야 합니다.", gpsDataTime, recentDataTime));
    }
}
