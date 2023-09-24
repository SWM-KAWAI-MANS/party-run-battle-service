package online.partyrun.partyrunbattleservice.domain.single.exception;

import online.partyrun.partyrunbattleservice.global.exception.NotFoundException;

public class SingleNotFoundException extends NotFoundException {

    public SingleNotFoundException(String singleId) {
        super(String.format("%s 의 싱글 기록은 존재하지 않습니다.", singleId));
    }
}
