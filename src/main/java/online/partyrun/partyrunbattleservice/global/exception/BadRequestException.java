package online.partyrun.partyrunbattleservice.global.exception;

public abstract class BadRequestException extends RuntimeException {
    protected BadRequestException() {
        super("잘못된 요청입니다.");
    }

    protected BadRequestException(String message) {
        super(message);
    }
}
