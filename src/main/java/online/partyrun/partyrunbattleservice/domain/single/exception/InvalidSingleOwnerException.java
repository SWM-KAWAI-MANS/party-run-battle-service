package online.partyrun.partyrunbattleservice.domain.single.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class InvalidSingleOwnerException extends BadRequestException {
    public InvalidSingleOwnerException(String singleId, String runnerId) {
        super(String.format("%s 러너는 %s 싱글 기록의 주인이 아닙니다.", runnerId, singleId));
    }
}
