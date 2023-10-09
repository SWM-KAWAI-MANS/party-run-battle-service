package online.partyrun.partyrunbattleservice.domain.mypage.dto;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.single.entity.Single;

import java.util.List;

public record MyPageHistoryResponse(List<MyPageRunningResponse> history) {

    public static MyPageHistoryResponse ofBattles(List<Battle> battles, String memberId) {
        return new MyPageHistoryResponse(
                battles.stream()
                        .map(battle -> MyPageRunningResponse.of(battle, memberId))
                        .toList()
        );
    }

    public static MyPageHistoryResponse ofSingle(List<Single> singles) {
        return new MyPageHistoryResponse(
                singles.stream()
                        .map(MyPageRunningResponse::from)
                        .toList()
        );
    }
}
