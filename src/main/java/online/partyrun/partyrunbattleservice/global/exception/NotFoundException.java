package online.partyrun.partyrunbattleservice.global.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("요청한 리소스를 찾을 수 없습니다.");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
