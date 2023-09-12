package online.partyrun.partyrunbattleservice.domain.single.entity;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class IllegalRunningTimeException extends BadRequestException {

    public IllegalRunningTimeException(int hours, int minutes, int seconds) {
        super(String.format("RunningTime은 %d시 %d분 %d초 일 수 없습니다.", hours, minutes, seconds));
    }
}
