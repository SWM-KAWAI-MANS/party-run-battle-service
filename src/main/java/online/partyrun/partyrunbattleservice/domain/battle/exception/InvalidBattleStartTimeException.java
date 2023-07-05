package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

import java.time.LocalDateTime;

public class InvalidBattleStartTimeException extends BadRequestException {
    public InvalidBattleStartTimeException(LocalDateTime startTime, LocalDateTime createdAt) {
        super(String.format("배틀 시작 시간 %s는 배틀 생성 시간 %s 보다 이후여야합니다.", startTime, createdAt));
    }
}
