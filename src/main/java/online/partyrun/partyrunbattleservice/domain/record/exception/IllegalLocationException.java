package online.partyrun.partyrunbattleservice.domain.record.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class IllegalLocationException extends BadRequestException {
    public IllegalLocationException(double value, double maxValue, double minValue) {
        super(String.format("%s 값은 %s 이상 %s 이하여야 합니다.", value, minValue, maxValue));
    }
}
