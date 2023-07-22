package online.partyrun.partyrunbattleservice.domain.record.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class IllegalRecordDistanceException extends BadRequestException {
    public IllegalRecordDistanceException(double distance) {
        super(String.format("Record의 거리는 %s 미만일 수 없습니다.", distance));
    }
}
