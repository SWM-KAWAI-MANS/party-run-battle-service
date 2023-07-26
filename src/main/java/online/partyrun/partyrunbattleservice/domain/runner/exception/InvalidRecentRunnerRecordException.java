package online.partyrun.partyrunbattleservice.domain.runner.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class InvalidRecentRunnerRecordException extends BadRequestException {
    public InvalidRecentRunnerRecordException(String runnerId) {
        super(String.format("%s 러너의 최근 Gps 기록이 존재하지 않습니다.", runnerId));
    }
}
