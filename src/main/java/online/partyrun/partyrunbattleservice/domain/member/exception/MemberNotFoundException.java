package online.partyrun.partyrunbattleservice.domain.member.exception;

import online.partyrun.partyrunbattleservice.global.exception.NotFoundException;

public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException(String memberId) {
        super(String.format("%s 아이디의 멤버를 찾을 수 없습니다.", memberId));
    }
}
