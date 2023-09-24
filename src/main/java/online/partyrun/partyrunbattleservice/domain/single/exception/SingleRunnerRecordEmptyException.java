package online.partyrun.partyrunbattleservice.domain.single.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class SingleRunnerRecordEmptyException extends BadRequestException {

    public SingleRunnerRecordEmptyException() {
        super("Single 모드의 Runner 기록은 빈 값일 수 없습니다.");
    }
}
