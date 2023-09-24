package online.partyrun.partyrunbattleservice.domain.member.service;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

import java.util.List;

public class InvalidMemberException extends BadRequestException {
    public InvalidMemberException(List<String> runnerIds) {
        super(String.format("%s에 존재하지 않는 유저가 포함되어 있습니다.", runnerIds));
    }
}
